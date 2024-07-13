package io.dbkover.junit5

import io.dbkover.junit5.annotation.DBKoverConnection
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.util.AnnotationUtils
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties


internal fun <T : Annotation> ExtensionContext.hasAnnotation(annotation: Class<T>): Boolean =
    AnnotationUtils.isAnnotated(element, annotation)

internal fun <T : Annotation> ExtensionContext.findAnnotation(annotation: Class<T>): T? =
    AnnotationUtils.findAnnotation(element, annotation).orElse(null)

internal fun <T : Annotation> ExtensionContext.findAnnotationThrowing(annotation: Class<T>): T =
    findAnnotation(annotation) ?: throw RuntimeException("Annotation '${annotation.name}' not present")

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
    val url: String? = System.getProperty("datasources.default.url")
    val username: String? = System.getProperty("datasources.default.username")
    val password: String? = System.getProperty("datasources.default.password")

    if (url == null || username == null || password == null) {
        return null
    }

    val props = Properties()
    props.setProperty("user", username)
    props.setProperty("password", password)

    return DriverManager.getConnection(url, props)
}
