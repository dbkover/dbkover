package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@MustBeDocumented
@Inherited
annotation class DBKoverConnectionConfig(
    /**
     * The name of the connection.
     * Using 'default' as the fallback name.
     */
    val name: String = "default",
)
