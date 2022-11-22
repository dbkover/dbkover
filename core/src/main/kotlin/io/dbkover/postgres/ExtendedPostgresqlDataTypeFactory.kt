package io.dbkover.postgres

import io.dbkover.postgres.type.JsonbDataType
import org.dbunit.dataset.datatype.DataType
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory

class ExtendedPostgresqlDataTypeFactory : PostgresqlDataTypeFactory() {
    override fun createDataType(sqlType: Int, sqlTypeName: String?): DataType {
        return when (sqlTypeName) {
            "jsonb" -> JsonbDataType()
            else -> super.createDataType(sqlType, sqlTypeName)
        }
    }
}
