plugins {
    dbkover.library
    dbkover.publishing
}

val dbunit_version: String by project

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.dbunit:dbunit:$dbunit_version")
}
