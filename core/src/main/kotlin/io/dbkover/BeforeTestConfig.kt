package io.dbkover

data class BeforeTestConfig(
    val connection: String = "default",
    val schema: String,
    val seedPath: List<String>,
    val cleanTables: Boolean = true,
    val cleanIgnoreTables: List<String> = emptyList(),
)