package io.dbkover

import java.sql.Connection

data class ExecutionConfig(
    @Deprecated("Use connectionConfigs instead")
    val connectionFactory: (() -> Connection)? = null,

    @Deprecated("Use connectionConfigs instead")
    val connectionConfig: ConnectionConfig? = null,

    val connectionConfigs: Map<String, ConnectionConfig> = emptyMap(),
)
