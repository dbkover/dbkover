---
title: "Quick Start"
description: "Summary of how to get started with DBKover."
lead: "Summary of how to get started with DBKover."
date: 2020-11-16T13:59:39+01:00
lastmod: 2020-11-16T13:59:39+01:00
draft: false
images: []
weight: 110
toc: true
---

## Requirements

- JDK 9
- JUnit 5

## Install DBKover

Include DBKover with Gradle or Maven as a test dependency.

Latest version on Maven Central: {{< badge_maven >}}

### Gradle

```kotlin
// build.gradle.kts
dependencies {
    // ...
    testImplementation("io.dbkover:junit5:$dbKoverVersion")
    // ...
}
```

### Maven

```xml
<!-- pom.xml -->
<dependency>
    <groupId>io.dbkover</groupId>
    <artifactId>junit5</artifactId>
    <version>${dbKoverVersion}</version>
    <scope>test</scope>
</dependency>
```

## Creating the first test

DBKover requires you to annotate the test class with `@DBKover` annotation.
This enables DBKover to inject into the test and apply database changes prior and after tests.

It also requires you to specify the connection factory of the database with `@DBKoverConnection`.

```kotlin
@DBKover
class SomeDatabaseTest {

  @DBKoverConnection
  fun getConnect(): Connection {
    // ...
  }

}
```

### Injecting data before test

Within a DBKover test class you can specify one or multiple data sets for populating the database.
This is done with the `@DBKoverDataSet` annotation.

```kotlin
@DBKoverDataSet(paths = ["dataset.xml"])
fun `Some test`() {
    // ...
}
```

### Assert database content

It is also possible to assert the content of a database based on a data set.
This is done with the `@DBKoverExpected` annotation.

```kotlin
@DBKoverExpected("expected_dataset.xml")
fun `Some test`() {
    // Manipulate the database
}
```
