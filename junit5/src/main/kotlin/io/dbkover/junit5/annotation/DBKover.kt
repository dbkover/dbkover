package io.dbkover.junit5.annotation

import io.dbkover.junit5.DBKoverExtension
import org.junit.jupiter.api.extension.ExtendWith
import java.lang.annotation.Inherited

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@ExtendWith(DBKoverExtension::class)
annotation class DBKover
