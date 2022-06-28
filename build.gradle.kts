group = "io.dbkover"
version = "0.1.0"

subprojects {
    group = rootProject.group
    version = rootProject.version

    repositories {
        mavenCentral()
    }
}

tasks.create("version") {
    doLast {
        println(version)
    }
}
