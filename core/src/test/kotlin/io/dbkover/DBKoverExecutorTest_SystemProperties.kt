package io.dbkover

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer

internal class DBKoverExecutorTest_SystemProperties {

    private val dbKoverExecutor = DBKoverExecutor()

    @Test
    fun `DBKoverExecutor can prepare database prior to test`() {
        dbKoverExecutor.beforeTest(
            BeforeTestConfig(
                schema = "public",
                seedPath = listOf("test.xml", "test2.xml"),
                cleanTables = false,
            )
        )

        val result = db.createConnection("").use {
            it.prepareStatement("SELECT * FROM test;").executeQuery()
        }

        assertTrue { result.next() }

        assertEquals(1, result.getLong("id"))
        assertEquals("Test 1", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))
        assertNull(result.getString("information"))
        assertNull(result.getString("test_enum"))

        assertTrue { result.next() }

        assertEquals(2, result.getLong("id"))
        assertEquals("Test 2", result.getString("name"))
        assertNull(result.getString("description"))
        assertEquals("{\"hello\": \"world\"}", result.getString("information"))
        assertEquals("value_1", result.getString("test_enum"))

        assertTrue { result.next() }

        assertEquals(3, result.getLong("id"))
        assertEquals("Test 3", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))
        assertEquals("{\"hello\": \"world\"}", result.getString("information"))
        assertEquals("value_1", result.getString("test_enum"))

        assertFalse { result.next() }
    }

    @Test
    fun `DBKoverExecutor can prepare database prior to test on multiple connections`() {
        dbKoverExecutor.beforeTest(
            BeforeTestConfig(
                schema = "public",
                seedPath = listOf("test.xml", "test2.xml"),
                cleanTables = false,
            )
        )

        dbKoverExecutor.beforeTest(
            BeforeTestConfig(
                connection = "other",
                schema = "public",
                seedPath = listOf("test.xml", "test2.xml"),
                cleanTables = false,
            )
        )

        val result = db.createConnection("").use {
            it.prepareStatement("SELECT * FROM test;").executeQuery()
        }

        assertTrue { result.next() }

        assertEquals(1, result.getLong("id"))
        assertEquals("Test 1", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))
        assertNull(result.getString("information"))
        assertNull(result.getString("test_enum"))

        assertTrue { result.next() }

        assertEquals(2, result.getLong("id"))
        assertEquals("Test 2", result.getString("name"))
        assertNull(result.getString("description"))
        assertEquals("{\"hello\": \"world\"}", result.getString("information"))
        assertEquals("value_1", result.getString("test_enum"))

        assertTrue { result.next() }

        assertEquals(3, result.getLong("id"))
        assertEquals("Test 3", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))
        assertEquals("{\"hello\": \"world\"}", result.getString("information"))
        assertEquals("value_1", result.getString("test_enum"))

        assertFalse { result.next() }

        val resultDb2 = db2.createConnection("").use {
            it.prepareStatement("SELECT * FROM test;").executeQuery()
        }

        assertTrue { resultDb2.next() }

        assertEquals(1, resultDb2.getLong("id"))
        assertEquals("Test 1", resultDb2.getString("name"))
        assertEquals("Descriptive text", resultDb2.getString("description"))
        assertNull(resultDb2.getString("information"))
        assertNull(resultDb2.getString("test_enum"))

        assertTrue { resultDb2.next() }

        assertEquals(2, resultDb2.getLong("id"))
        assertEquals("Test 2", resultDb2.getString("name"))
        assertNull(resultDb2.getString("description"))
        assertEquals("{\"hello\": \"world\"}", resultDb2.getString("information"))
        assertEquals("value_1", resultDb2.getString("test_enum"))

        assertTrue { resultDb2.next() }

        assertEquals(3, resultDb2.getLong("id"))
        assertEquals("Test 3", resultDb2.getString("name"))
        assertEquals("Descriptive text", resultDb2.getString("description"))
        assertEquals("{\"hello\": \"world\"}", resultDb2.getString("information"))
        assertEquals("value_1", resultDb2.getString("test_enum"))

        assertFalse { resultDb2.next() }
    }

    @Test
    fun `DBKoverExecutor can prepare database in specific schema prior to test`() {
        dbKoverExecutor.beforeTest(
            BeforeTestConfig(
                schema = "test_schema",
                seedPath = listOf("test_schema.test.xml"),
                cleanTables = false,
            )
        )

        val result = db.createConnection("").use {
            it.prepareStatement("SELECT * FROM test_schema.test2;").executeQuery()
        }

        assertTrue { result.next() }

        assertEquals(1, result.getLong("id"))
        assertEquals("Test 1", result.getString("name"))
        assertEquals("Descriptive text", result.getString("description"))
        assertEquals("{\"hello\": \"world\"}", result.getString("information"))

        assertFalse { result.next() }
    }

    @Test
    fun `DBKoverExecutor can check expected database after test`() {
        db.createConnection("").use {
            it.prepareStatement("DELETE FROM test;").execute()
            it.prepareStatement("INSERT INTO test (id, name, description) VALUES (1, 'Test 1', 'Descriptive text');").execute()
            it.prepareStatement("INSERT INTO test (id, name, information, test_enum) VALUES (2, 'Test 2', '{\"hello\": \"world\"}', 'value_1');").execute()
        }

        dbKoverExecutor.afterTest(
            AfterTestConfig(
                schema = "public",
                expectedPath = "test.xml",
            )
        )
    }

    @Test
    fun `DBKoverExecutor can check expected database in specific schema after test`() {
        db.createConnection("").use {
            it.prepareStatement("DELETE FROM test_schema.test2;").execute()
            it.prepareStatement("INSERT INTO test_schema.test2 (id, name, description, information, test_enum) VALUES (1, 'Test 1', 'Descriptive text', '{\"hello\": \"world\"}', 'value_1');").execute()
        }

        dbKoverExecutor.afterTest(
            AfterTestConfig(
                schema = "test_schema",
                expectedPath = "test_schema.test.xml",
            )
        )
    }

    @Test
    fun `DBKoverExecutor can clean data before test`() {
        db.createConnection("").use {
            it.createStatement().execute("DELETE FROM test;")
            it.createStatement().execute("INSERT INTO test (id, name, description) VALUES (1, 'Test 1', 'Descriptive text');")
            it.createStatement().execute("INSERT INTO test (id, name) VALUES (2, 'Test 2');")
            it.createStatement().execute("INSERT INTO test_dont_clean (id, name) VALUES (1, 'Test dont clean');")
            it.createStatement().execute("INSERT INTO test_foreign (id, test_id) VALUES (1, 1);")
        }

        dbKoverExecutor.beforeTest("public", listOf(), true, listOf("test_dont_clean"))

        val result = db.createConnection("").use {
            it.createStatement().executeQuery("SELECT * FROM test;")
        }

        assertFalse { result.next() }

        val resultDontClean = db.createConnection("").use {
            it.createStatement().executeQuery("SELECT * FROM test_dont_clean;")
        }

        assertTrue { resultDontClean.next() }

        assertEquals(1, resultDontClean.getLong("id"))
        assertEquals("Test dont clean", resultDontClean.getString("name"))
        assertNull(resultDontClean.getString("description"))

        assertFalse { resultDontClean.next() }
    }

    companion object {
        @JvmStatic
        private val db = PostgreSQLContainer<Nothing>("postgres:16-alpine")

        @JvmStatic
        private val db2 = PostgreSQLContainer<Nothing>("postgres:16-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            db.start()
            db2.start()

            System.setProperty("dbkover.connection.default.url", db.jdbcUrl)
            System.setProperty("dbkover.connection.default.username", db.username)
            System.setProperty("dbkover.connection.default.password", db.password)

            System.setProperty("dbkover.connection.other.url", db2.jdbcUrl)
            System.setProperty("dbkover.connection.other.username", db2.username)
            System.setProperty("dbkover.connection.other.password", db2.password)

            db.createConnection("").use {
                it.prepareStatement("""
                    CREATE TYPE a_test_enum AS ENUM ('value_1', 'value_2');
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE TABLE test (
                        id bigint primary key,
                        name varchar(60) not null,
                        description varchar(255),
                        information jsonb,
                        test_enum a_test_enum
                    );
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE TABLE test_dont_clean (
                        id bigint primary key,
                        name varchar(60) not null,
                        description varchar(255)
                    );
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE TABLE test_foreign (
                        id bigint primary key,
                        test_id bigint not null,
                        CONSTRAINT fk_test
                              FOREIGN KEY(test_id) 
                        	  REFERENCES test(id)

                    );
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE SCHEMA IF NOT EXISTS test_schema;
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE TYPE test_schema.b_test_enum AS ENUM ('value_1', 'value_2');
                """.trimIndent()).execute()

                it.prepareStatement("""
                    CREATE TABLE test_schema.test2 (
                        id bigint primary key,
                        name varchar(60) not null,
                        description varchar(255),
                        information jsonb,
                        test_enum test_schema.b_test_enum
                    );
                """.trimIndent()).execute()
            }

            db2.createConnection("").use {
                it.prepareStatement(
                    """
                    CREATE TYPE a_test_enum AS ENUM ('value_1', 'value_2');
                """.trimIndent()
                ).execute()

                it.prepareStatement(
                    """
                    CREATE TABLE test (
                        id bigint primary key,
                        name varchar(60) not null,
                        description varchar(255),
                        information jsonb,
                        test_enum a_test_enum
                    );
                """.trimIndent()
                ).execute()
            }
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            db.stop()
        }
    }

}