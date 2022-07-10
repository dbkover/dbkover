package io.dbkover

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer

internal class DBKoverExecutorTest {

    private val dbKoverExecutor = DBKoverExecutor(
        ExecutionConfig { db.createConnection("") }
    )

    @Test
    fun `DBKoverExecutor can prepare database prior to test`() {
        dbKoverExecutor.beforeTest(listOf("test.xml", "test2.xml"))

        val result = db.createConnection("").use {
            it.prepareStatement("SELECT * FROM test;").executeQuery()
        }

        assertTrue { result.next() }

        assertEquals(1, result.getLong("id"))
        assertEquals("Test 1", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))

        assertTrue { result.next() }

        assertEquals(2, result.getLong("id"))
        assertEquals("Test 2", result.getString("name"))
        assertNull(result.getString("description"))

        assertTrue { result.next() }

        assertEquals(3, result.getLong("id"))
        assertEquals("Test 3", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))

        assertFalse { result.next() }
    }

    @Test
    fun `DBKoverExecutor can check expected database after test`() {
        db.createConnection("").use {
            it.prepareStatement("DELETE FROM test;").execute()
            it.prepareStatement("INSERT INTO test (id, name, description) VALUES (1, 'Test 1', 'Descriptive text');").execute()
            it.prepareStatement("INSERT INTO test (id, name) VALUES (2, 'Test 2');").execute()
        }

        dbKoverExecutor.afterTest("test.xml", arrayOf())
    }

    companion object {
        @JvmStatic
        private val db = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            db.start()

            db.createConnection("").use {
                it.prepareStatement("""
                    CREATE TABLE test (
                        id bigint primary key,
                        name varchar(60) not null,
                        description varchar(255)
                    );
                """.trimIndent()).execute()
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            db.stop()
        }
    }

}