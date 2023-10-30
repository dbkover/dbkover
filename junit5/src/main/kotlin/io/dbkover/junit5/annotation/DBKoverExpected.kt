package io.dbkover.junit5.annotation

import java.lang.annotation.*

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
annotation class DBKoverExpected(
    val path: String,
    val ignoreColumns: Array<String> = ["id", "created_at", "updated_at"],

    /**
     * The schema in the database to compare
     * the dataset with.
     */
    val schema: String = "public",
)
