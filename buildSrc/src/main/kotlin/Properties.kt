import org.jetbrains.kotlin.konan.properties.hasProperty
import java.io.File
import java.util.Properties

fun Properties.setFromEnvVar(propKey: String, envVar: String) {
    System.getenv(envVar)?.let { setProperty(propKey, it) }
}

fun Properties.loadFromFile(file: File, required: Boolean = false) {
    if (required && !file.exists()) {
        throw RuntimeException("Properties file at ${file.path} does not exist")
    }

    if (file.exists()) {
        file.reader().use { load(it) }
    }
}

fun Properties.hasAnyProperties(vararg propKeys: String): Boolean {
    return propKeys.toList().any { hasProperty(it) }
}

object Sonatype {
    val Username = "sonatype.username"
    val Password = "sonatype.password"
    val RepositoryId = "sonatype.repositoryId"
}

object Signing {
    val Key = "signing.key"
    val KeyPath = "signing.keyPath"
    val KeyId = "signing.keyId"
    val Password = "signing.password"
}
