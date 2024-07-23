package io.dbkover.junit5

import io.dbkover.junit5.annotation.DBKoverConnection
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties


internal fun <T : Annotation> ExtensionContext.findAnnotations(annotation: Class<T>): List<T>
        = AnnotationUtils.findRepeatableAnnotations(element, annotation)

internal fun ExtensionContext.findDataBaseConnectionFactory(): () -> Connection {
    val connectionMethod = requiredTestClass.findMethodsWithAnnotation(DBKoverConnection::class.java)
        .firstOrNull { it.returnType == Connection::class.java }

    if (connectionMethod?.trySetAccessible() == true) {
        return {
            connectionMethod.invoke(requiredTestInstance) as Connection?
                ?: throw RuntimeException("Connection not initialized correctly")
        }
    } else if (connectionMethod == null) {
        return { obtainConnectionFromProperties() ?: throw RuntimeException("Connection not initialized correctly") }
    }

    throw RuntimeException("Connection method is missing")
}

private fun obtainConnectionFromProperties(): Connection? {
    val url: String? = System.getProperty("dbkover.connection.url")
    val username: String? = System.getProperty("dbkover.connection.username")
    val password: String? = System.getProperty("dbkover.connection.password")

    if (url == null || username == null || password == null) {
        return null
    }

    return DriverManager.getConnection(url, username, password)
}
