package io.dbkover

import org.dbunit.Assertion.assertEqualsIgnoreCols
import org.dbunit.DefaultDatabaseTester
import org.dbunit.database.DatabaseConfig
import org.dbunit.database.DatabaseConnection
import org.dbunit.dataset.CompositeDataSet
import org.dbunit.dataset.SortedTable
import org.dbunit.dataset.filter.DefaultColumnFilter
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory
import java.sql.Connection

class DBKoverExecutor(
    private val executionConfig: ExecutionConfig,
) {
    fun beforeTest(seedPath: List<String>) {
        val databaseConnection = getDatabaseConnection()

        DefaultDatabaseTester(databaseConnection).apply {
            dataSet = CompositeDataSet(seedPath.map { readDataSet(it) }.toTypedArray())
            onSetup()
        }
    }

    fun afterTest(expectedPath: String, ignoreColumns: Array<String>) {
        val dataSetExpected = readDataSet(expectedPath)
        val dataSetCurrent = getDatabaseConnection().createDataSet()

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

    private fun getConfigConnection(): Connection {
        return executionConfig.connectionFactory().apply {
            autoCommit = true
        }
    }

    private fun getDatabaseConnection() = DatabaseConnection(getConfigConnection(), "public").apply {
        config.setProperty(DatabaseConfig.FEATURE_ALLOW_EMPTY_FIELDS, true)
        config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, true)
        config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, PostgresqlDataTypeFactory())
    }

    private fun readDataSet(path: String) =
        javaClass.classLoader.getResourceAsStream(path)
            ?.let {
                FlatXmlDataSetBuilder()
                    .setCaseSensitiveTableNames(true)
                    .build(it)
            }
            ?: throw RuntimeException("Missing dataset at: $path")
}
