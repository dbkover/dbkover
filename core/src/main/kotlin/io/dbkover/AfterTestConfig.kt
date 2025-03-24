package io.dbkover

data class AfterTestConfig(
    val connection: String = "default",
    val schema: String,
    val expectedPath: String,
    val ignoreColumns: List<String> = emptyList(),
)