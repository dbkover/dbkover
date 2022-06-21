package io.dbkover

import io.dbkover.junit5.annotation.DBKover
import io.dbkover.junit5.annotation.DBKoverConnection
import io.dbkover.junit5.annotation.DBKoverDataSet
import io.dbkover.junit5.annotation.DBKoverExpected
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.DriverManager


@DBKover
class DBKoverTests {

    @Test
    @DBKoverDataSet("test.xml")
    @DBKoverExpected("test.xml")
    fun someLibraryMethodReturnsTrue() {
        println("In test")
    }

    companion object {
        @JvmStatic
        private val db = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            println("Start container")
            db.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            db.stop()
        }

        @DBKoverConnection
        @JvmStatic
        fun createConnection() = DriverManager.getConnection(db.jdbcUrl, db.username, db.password)
    }
}
