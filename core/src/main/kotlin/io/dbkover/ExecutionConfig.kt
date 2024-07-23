package io.dbkover

import java.sql.Connection

data class ExecutionConfig(
    @Deprecated("Use connectionConfig instead")
    val connectionFactory: (() -> Connection)?,
    val connectionConfig: ConnectionConfig?,
)
