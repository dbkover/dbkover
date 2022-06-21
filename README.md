# DBKover

DBKover is a library to enable easy integration testing to databases using DBUnit.

DBKover includes the following features:
- Ensure database has proper state before test
- Expect a given state of the database after test

DBKover offers integrations with:
- Junit 5

## Installation

Get this package with [JitPack](https://jitpack.io).
Include JitPack as a repository.

```kotlin
repositories {
    maven {
        url = uri("https://jitpack.io")
    }
}
```

Then include the library as a test dependency.

```kotlin
dependencies {
    // ...
    testImplementation("com.github.dbkover:dbkover:$dbKoverVersion")
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
