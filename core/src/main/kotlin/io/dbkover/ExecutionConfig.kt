package io.dbkover

import java.sql.Connection

data class ExecutionConfig(
    val connectionFactory: () -> Connection,
)
