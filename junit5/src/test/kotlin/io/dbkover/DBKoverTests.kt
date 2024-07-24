package io.dbkover

import io.dbkover.junit5.annotation.*
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager


private val db = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
    start()
}

@DBKover
class DBKoverTests {

    @Test
    @DBKoverDataSet("test.xml")
    @DBKoverExpected("test.xml")
    fun `Can run full test with Junit5`() {
        println("In test")
    }

    @Test
    @DBKoverDataSet(paths = ["test.xml", "test2.xml"])
    @DBKoverExpected("test.xml")
    fun `Can run full test with Junit5 and multiple paths`() {
        println("In test")
    }

    @Test
    @DBKoverDataSet(paths = ["test.xml"])
    @DBKoverDataSet(paths = ["test2.xml"])
    @DBKoverExpected("test.xml")
    @DBKoverExpected("test2.xml")
    fun `Can run full test with Junit5 and multiple annotations`() {
        println("In test")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            db.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            db.stop()
        }

        @DBKoverConnectionConfig
        @JvmStatic
        fun connectionConfig() = ConnectionConfig(db.jdbcUrl, db.username, db.password)
    }
}
