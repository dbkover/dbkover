plugins {
    dbkover.library
    dbkover.publishing
}

tasks.test {
    useJUnitPlatform()
}

val junit_version: String by project

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":core"))

    implementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    implementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    implementation("org.junit.jupiter:junit-jupiter-engine:$junit_version")

    testImplementation("org.testcontainers:postgresql:1.17.2")
}
