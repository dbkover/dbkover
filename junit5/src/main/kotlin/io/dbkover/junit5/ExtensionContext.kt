package io.dbkover.junit5

import io.dbkover.junit5.annotation.DBKoverConnection
import org.junit.jupiter.api.extension.*
import org.junit.platform.commons.util.*
import java.sql.*

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
    }

    throw RuntimeException("Connection method is missing")
}
