plugins {
    dbkover.library
    dbkover.publishing
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation(project(":core"))

    implementation(libs.junit.api)

    testImplementation(libs.testcontainers.postgres)
}
