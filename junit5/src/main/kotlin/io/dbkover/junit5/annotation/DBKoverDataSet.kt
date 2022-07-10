package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class DBKoverDataSet(
    @Deprecated(message = "Deprecated for adding multi file support", ReplaceWith("paths"))
    val path: String = "",

    val paths: Array<String> = [],
)
