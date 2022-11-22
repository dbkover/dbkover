# DBKover

![](https://img.shields.io/maven-central/v/io.dbkover/core)
![](https://img.shields.io/github/workflow/status/dbkover/dbkover/Build%20and%20test/main)
![](https://img.shields.io/github/license/dbkover/dbkover)

DBKover is a library to enable easy integration testing to databases using DBUnit.

DBKover includes the following features:
- Ensure database has proper state before test
- Expect a given state of the database after test

DBKover offers integrations with:
- Junit 5

> **Warning**
> 
> The API is still during development and may change without further notice until v1.

## Installation

Get this package on Maven Central.
Include the module you need as a test dependency.
E.g. for JUnit 5, see below example.

```kotlin
dependencies {
    // ...
    testImplementation("io.dbkover:junit5:$dbKoverVersion")
    // ...
}
```

## Documentation

To use DBKover the test class must be annotated with `@DBKover` and specify a database connection with `@DBKoverConnection`.

```kotlin
@DBKover
class SomeDatabaseTest {
    
    @DBKoverConnection
    fun getConnect(): Connection {
        // ...
    }
    
}
```

Tests can be annotated with `@DBKoverDataSet` and `@DBKoverExpected` to ensure data before and after tests.

```xml
<!-- dataset.xml in resources -->
<dataset>
    <table_name name="DBKover"/>
</dataset>
```

```kotlin
@DBKoverDataSet("dataset.xml")
fun `Test that DBKover exists in table`() {
    // ...
}

@DBKoverExpected("dataset.xml")
fun `Test that DBKover is in database after insert`() {
    // ...
}
```
