package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class DBKoverDataSet(
    val path: String
)
