package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Deprecated("Use DBKoverConnectionConfig instead")
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class DBKoverConnection
