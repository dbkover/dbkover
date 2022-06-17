package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class DBKoverConnection
