package io.dbkover.postgres

import io.dbkover.postgres.type.JsonbDataType
import org.dbunit.dataset.datatype.DataType
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory
import java.sql.Types

class ExtendedPostgresqlDataTypeFactory(
    private val enums: List<String>,
) : PostgresqlDataTypeFactory() {
    override fun createDataType(sqlType: Int, sqlTypeName: String?): DataType {
        return when {
            sqlTypeName == "jsonb" -> JsonbDataType()
            isEnumType(sqlTypeName) -> super.createDataType(Types.OTHER, sqlTypeName)
            else -> super.createDataType(sqlType, sqlTypeName)
        }
    }

    override fun isEnumType(sqlTypeName: String?): Boolean {
        return enums.any { it == sqlTypeName }
    }
}
