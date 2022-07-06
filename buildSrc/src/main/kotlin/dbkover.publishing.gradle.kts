import java.util.Properties

plugins {
    `maven-publish`
    signing
}

val props = Properties().apply {
    loadFromFile(rootProject.file("local.properties"), required = false)

    setFromEnvVar(Sonatype.Username, "SONATYPE_USERNAME")
    setFromEnvVar(Sonatype.Password, "SONATYPE_PASSWORD")
    setFromEnvVar(Sonatype.RepositoryId, "SONATYPE_REPOSITORY_ID")

    setFromEnvVar(Signing.Key, "SIGNING_KEY")
    setFromEnvVar(Signing.KeyPath, "SIGNING_KEYPATH")
    setFromEnvVar(Signing.KeyId, "SIGNING_KEYID")
    setFromEnvVar(Signing.Password, "SIGNING_PASSWORD")
}

publishing {
    repositories {
        maven {
            name = "SonaType"
            url = props.getProperty(Sonatype.RepositoryId)
                ?.let { uri("https://s01.oss.sonatype.org/service/local/staging/deployByRepositoryId/$it") }
                ?: uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")

            credentials {
                username = props.getProperty(Sonatype.Username)
                password = props.getProperty(Sonatype.Password)
            }
        }
    }

    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])

            pom {
                name.set("DBKover")
                description.set("DBKover is a library to enable easy integration testing to databases using DBUnit")
                url.set("https://github.com/dbkover/dbkover")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                scm {
                    url.set("https://github.com/dbkover/dbkover")
                }

                developers {
                    developer {
                        name.set("Magnus Biolzi Hansen")
                        email.set("mbh@dbkover.io")
                    }
                }
            }
        }
    }
}

if (props.hasAnyProperties(Signing.Key, Signing.KeyPath)) {
    signing {
        val signingKey = props.getProperty(Signing.Key) ?: run {
            val signingKeyPath = props.getProperty(Signing.KeyPath)
            file(signingKeyPath!!).readText()
        }

        val signingKeyId = props.getProperty(Signing.KeyId)
        val signingPassword = props.getProperty(Signing.Password)

        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
