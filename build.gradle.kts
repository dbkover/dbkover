plugins {
    id("pl.allegro.tech.build.axion-release") version "1.18.18"
}

group = "io.dbkover"
version = scmVersion.version


subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}
