plugins {
    id("org.jetbrains.kotlin.jvm") version "1.5.31"
    `java-library`
}

repositories {
    mavenCentral()
}

val dbunit_version: String by project

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.dbunit:dbunit:$dbunit_version")
}
