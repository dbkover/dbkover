package io.dbkover.postgres.type

import org.dbunit.dataset.datatype.AbstractDataType
import org.postgresql.util.PGobject
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Types

class JsonbDataType : AbstractDataType("jsonb", Types.OTHER, String::class.java, false) {
    override fun typeCast(obj: Any?): Any = obj.toString()

    override fun getSqlValue(column: Int, resultSet: ResultSet): Any? = resultSet.getString(column)

    override fun setSqlValue(value: Any?, column: Int, statement: PreparedStatement) {
        val jsonObj = PGobject().apply {
            this.type = "jsonb"
            this.value = value?.toString()
        }
        statement.setObject(column, jsonObj)
    }
}
