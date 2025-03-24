package io.dbkover.junit5

import io.dbkover.ConnectionConfig
import io.dbkover.junit5.annotation.DBKoverConnection
import io.dbkover.junit5.annotation.DBKoverConnectionConfig
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import java.sql.Connection


internal fun <T : Annotation> ExtensionContext.findAnnotations(annotation: Class<T>): List<T>
        = AnnotationUtils.findRepeatableAnnotations(element, annotation)

internal fun ExtensionContext.findDataBaseConnectionConfigs(): Map<String,ConnectionConfig> {
    val connectionConfigMethods = requiredTestClass.findMethodsWithAnnotation(DBKoverConnectionConfig::class.java)
        .filter{ it.returnType == ConnectionConfig::class.java }

    return connectionConfigMethods.mapNotNull { connectionConfigMethod ->
        val annotation = connectionConfigMethod.getAnnotation(DBKoverConnectionConfig::class.java)
        if (connectionConfigMethod?.trySetAccessible() == true) {
            annotation.name to connectionConfigMethod.invoke(requiredTestInstance) as ConnectionConfig
        } else {
            null
        }
    }.toMap()
}

internal fun ExtensionContext.findDataBaseConnectionFactory(): (() -> Connection)? {
    val connectionMethod = requiredTestClass.findMethodsWithAnnotation(DBKoverConnection::class.java)
        .firstOrNull { it.returnType == Connection::class.java }

    if (connectionMethod?.trySetAccessible() == true) {
        return {
            connectionMethod.invoke(requiredTestInstance) as Connection?
                ?: throw RuntimeException("Connection not initialized correctly")
        }
    }

    return null
}
