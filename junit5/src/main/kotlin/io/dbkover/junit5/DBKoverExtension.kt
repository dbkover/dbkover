package io.dbkover.junit5

import io.dbkover.AfterTestConfig
import io.dbkover.BeforeTestConfig
import io.dbkover.DBKoverExecutor
import io.dbkover.ExecutionConfig
import io.dbkover.junit5.annotation.DBKoverExpected
import io.dbkover.junit5.annotation.DBKoverDataSet
import io.dbkover.junit5.exception.ConfigValidationException
import org.junit.jupiter.api.extension.AfterTestExecutionCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback
import org.junit.jupiter.api.extension.ExtensionContext

class DBKoverExtension : BeforeAllCallback, BeforeTestExecutionCallback, AfterTestExecutionCallback {
    override fun beforeAll(context: ExtensionContext) {
        context.setExecutor {
            val connection = context.findDataBaseConnectionFactory()
            val connectionConfigs = context.findDataBaseConnectionConfigs()
            DBKoverExecutor(ExecutionConfig(connection, connectionConfigs = connectionConfigs))
        }
    }

    override fun beforeTestExecution(context: ExtensionContext) {
        val dbKoverDataSets = context.findAnnotations(DBKoverDataSet::class.java)
        if (dbKoverDataSets.isEmpty()) {
            return
        }

        for (dbKoverDataSet in dbKoverDataSets) {
            dbKoverDataSet.validate()
            context.getExecutor().beforeTest(dbKoverDataSet.toBeforeTestConfig())
        }
    }

    override fun afterTestExecution(context: ExtensionContext) {
        val dbKoverExpecteds = context.findAnnotations(DBKoverExpected::class.java)
        if (dbKoverExpecteds.isEmpty()) {
            return
        }

        for (dbKoverExpected in dbKoverExpecteds) {
            context.getExecutor().afterTest(dbKoverExpected.toBeforeTestConfig())
        }
    }

    private fun DBKoverDataSet.validate() {
        if (path.isNotBlank() && paths.isNotEmpty()) {
            throw ConfigValidationException("'path' and 'paths' is mutual exclusive, please use 'paths' only")
        }
    }

    private fun DBKoverDataSet.toBeforeTestConfig() = BeforeTestConfig(
        connection = connection,
        schema = schema,
        seedPath = path.takeIf { it.isNotBlank() }?.let { listOf(it) } ?: paths.toList(),
        cleanTables = cleanBefore,
        cleanIgnoreTables = cleanBeforeIgnoreTables.toList()
    )

    private fun DBKoverExpected.toBeforeTestConfig() = AfterTestConfig(
        connection = connection,
        schema = schema,
        expectedPath = path,
        ignoreColumns = ignoreColumns.toList(),
    )

    private fun ExtensionContext.setExecutor(factory: () -> DBKoverExecutor) {
        val store = getStore(STORE_NAMESPACE)
        if (store.get(requiredTestClass, DBKoverExecutor::class.java) == null) {
            store.put(requiredTestClass, factory())
        }
    }

    private fun ExtensionContext.getExecutor(): DBKoverExecutor =
        getStore(STORE_NAMESPACE).get(requiredTestClass, DBKoverExecutor::class.java)!!

    companion object {
        private val STORE_NAMESPACE = ExtensionContext.Namespace.create(DBKoverExtension::class.java)
    }
}