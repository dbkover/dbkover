plugins {
    dbkover.library
    dbkover.publishing
}

tasks.test {
    useJUnitPlatform()
}

val dbunit_version: String by project
val junit_version: String by project

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.dbunit:dbunit:$dbunit_version")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junit_version")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junit_version")
    testImplementation("org.testcontainers:postgresql:1.17.2")
}
