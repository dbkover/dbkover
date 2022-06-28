import groovy.time.TimeCategory
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm")
    jacoco
}

java {
    withJavadocJar()
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

if (!rootProject.ext.has("testResults")) {
    rootProject.ext["testResults"] = mutableListOf<String>()
    rootProject.ext["testIsSuccess"] = true
}

tasks.withType<Test>() {
    finalizedBy(tasks.named("jacocoTestReport"))

    testLogging {
        events(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_OUT, TestLogEvent.STANDARD_ERROR)
        exceptionFormat = TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }

    ignoreFailures = true

    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
        if (desc.parent == null) {
            val summary = """
            ${project.name}:${name} results: ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} successes, ${result.failedTestCount} failures, ${result.skippedTestCount} skipped) in ${
                TimeCategory.minus(
                    Date(result.endTime),
                    Date(result.startTime)
                )
            }
            Report file: ${reports.html.entryPoint}
        """.trimIndent()

            if (result.resultType == TestResult.ResultType.SUCCESS) {
                (rootProject.ext["testResults"] as MutableList<String>).add(0, summary)
            } else {
                (rootProject.ext["testResults"] as MutableList<String>) += summary
                rootProject.ext["testIsSuccess"] = false
            }
        }
    }))
}

tasks.withType<JacocoReport> {
    dependsOn(tasks.named("test"))

    reports {
        xml.required.set(true)
    }
}

if (!rootProject.hasProperty("testsConfigured")) {
    rootProject.ext["testsConfigured"] = true
    rootProject.gradle.buildFinished {
        val results = rootProject.ext["testResults"] as List<String>

        if (results.isNotEmpty()) {
            printResults(results)
        }
        if (rootProject.ext["testIsSuccess"] == false) {
            throw GradleException("Tests failed")
        }
    }
}

fun printResults(results: List<String>) {
    val summaryList = results.mapIndexed { index, s -> if (index == results.size - 1) s else s + "\n" } // Empty line for each test result
        .flatMap { it.split("\n") }

    val spacing = 2
    val maxLength = summaryList.map { it.length }
        .reduce { acc, i -> if (i > acc) i else acc }

    val summary = summaryList.map { "| $it${" ".repeat(maxLength - it.length)} |" }
        .joinToString("\n")

    println("+${"-".repeat(maxLength + spacing)}+")
    println(summary)
    println("+${"-".repeat(maxLength + spacing)}+")
}
