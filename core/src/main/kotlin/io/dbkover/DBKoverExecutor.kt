package io.dbkover

import io.dbkover.postgres.ExtendedPostgresqlDataTypeFactory
import org.dbunit.Assertion.assertEqualsIgnoreCols
import org.dbunit.DefaultDatabaseTester
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.CompositeDataSet
import org.dbunit.dataset.SortedTable
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import java.sql.Connection
import java.sql.DriverManager

class DBKoverExecutor(
    private val executionConfig: ExecutionConfig,
) {
    fun beforeTest(schema: String, seedPath: List<String>, cleanTables: Boolean, cleanIgnoreTables: List<String>) {
        val databaseConnection = getDatabaseConnection(schema)

        if (cleanTables) {
            cleanTables(databaseConnection.connection, cleanIgnoreTables)
        }

        DefaultDatabaseTester(databaseConnection).apply {
            dataSet = CompositeDataSet(seedPath.map { readDataSet(it) }.toTypedArray())
            onSetup()
        }
    }

    fun afterTest(schema: String, expectedPath: String, ignoreColumns: Array<String>) {
        val dataSetExpected = readDataSet(expectedPath)
        val dataSetCurrent = getDatabaseConnection(schema).createDataSet()

        dataSetExpected.tableNames.forEach { tableNameExpected ->
            val tableExpected = SortedTable(dataSetExpected.getTable(tableNameExpected))
            val tableCurrent = SortedTable(dataSetCurrent.getTable(tableNameExpected))
            val tableFiltered = DefaultColumnFilter.includedColumnsTable(
                tableCurrent,
                tableExpected.tableMetaData.columns
            )
            assertEqualsIgnoreCols(tableExpected, tableFiltered, ignoreColumns)
        }
    }

    private fun cleanTables(connection: Connection, cleanIgnoreTables: List<String>) {
        val tableNamesResult = connection.metaData.getTables(null, null, "%", arrayOf("TABLE"))
        val deleteFromTables = mutableListOf<Pair<String, String>>()
        while (tableNamesResult.next()) {
            deleteFromTables += tableNamesResult.getString("TABLE_SCHEM") to tableNamesResult.getString("TABLE_NAME")
        }

        connection.createStatement().use { statement ->
            statement.execute("SET session_replication_role = replica;")

            try {
                deleteFromTables.filter { !cleanIgnoreTables.contains(it.second) }.forEach {
                    statement.execute("DELETE FROM ${"${it.first}.${it.second}"};")
                }
            } finally {
                statement.execute("SET session_replication_role = DEFAULT;")
            }
        }
    }

    private fun getConfigConnection(): Connection {
        val connection = executionConfig.connectionConfig?.toConnection()
            ?: executionConfig.connectionFactory?.invoke()
            ?: getConfigConnectionFromProperties()?.toConnection()
            ?: throw RuntimeException("Connection not initialized correctly")

        return connection.apply {
            autoCommit = true
        }
    }

    private fun ConnectionConfig.toConnection() = DriverManager.getConnection(jdbcUrl, username, password)

    private fun getConfigConnectionFromProperties(): ConnectionConfig? {
        val url: String? = System.getProperty("dbkover.connection.url")
        val username: String? = System.getProperty("dbkover.connection.username")
        val password: String? = System.getProperty("dbkover.connection.password")

        if (url == null || username == null || password == null) {
            return null
        }

        return ConnectionConfig(url, username, password)
    }

    private fun getDatabaseConnection(schema: String): DatabaseConnection {
        val connection = getConfigConnection()
        val enums = connection.getEnumTypes()

        return DatabaseConnection(connection, schema).apply {
            config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
            config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true)
            config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, ExtendedPostgresqlDataTypeFactory(enums))
        }
    }

    private fun readDataSet(path: String) =
        javaClass.classLoader.getResourceAsStream(path)
            ?.let {
                FlatXmlDataSetBuilder()
                    .setCaseSensitiveTableNames(true)
                    .setColumnSensing(true)
                    .build(it)
            }
            ?: throw RuntimeException("Missing dataset at: $path")

    private fun Connection.getEnumTypes(): List<String> {
        val enumTypes = mutableListOf<String>()

        val result = prepareStatement(
            """
                select n.nspname as enum_schema,
                       t.typname as enum_name
                from pg_type t
                join pg_enum e on t.oid = e.enumtypid
                join pg_catalog.pg_namespace n ON n.oid = t.typnamespace
                group by n.nspname, t.typname;
            """.trimIndent()
        ).executeQuery()

        while (result.next()) {
            val schema = result.getString("enum_schema")
            if (schema != "public") {
                enumTypes.add("\"${schema}\".\"${result.getString("enum_name")}\"")
            } else {
                enumTypes.add(result.getString("enum_name"))
            }
        }

        return enumTypes
    }
}
