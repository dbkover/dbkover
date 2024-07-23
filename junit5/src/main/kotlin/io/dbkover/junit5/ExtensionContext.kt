package io.dbkover.junit5

import io.dbkover.ConnectionConfig
import io.dbkover.junit5.annotation.DBKoverConnection
import io.dbkover.junit5.annotation.DBKoverConnectionConfig
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import java.sql.Connection


internal fun <T : Annotation> ExtensionContext.findAnnotations(annotation: Class<T>): List<T>
        = AnnotationUtils.findRepeatableAnnotations(element, annotation)

internal fun ExtensionContext.findDataBaseConnectionConfig(): ConnectionConfig? {
    val connectionConfigMethod = requiredTestClass.findMethodsWithAnnotation(DBKoverConnectionConfig::class.java)
        .firstOrNull { it.returnType == ConnectionConfig::class.java }

    if (connectionConfigMethod?.trySetAccessible() == true) {
        return connectionConfigMethod.invoke(requiredTestInstance) as ConnectionConfig?
                ?: throw RuntimeException("Connection not initialized correctly")
    }

    return null
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
