# Spark Development Guidelines

## Architecture
Root is a Maven multi-module project:
  - `core` contains the main Spark desktop app and startup logic.
  - `plugins/*` are optional runtime extensions that Spark provides by default.
  - `emoticons` contains the smiles packs (zip artifacts).
  - `distribution` has the InstallJ installer/distribution packaging configuration.

Main entry point is `org.jivesoftware.Spark` defined in `core/pom.xml` manifest.

### Internationalization
New localized strings should be added to `src/main/resources/i18n/spark_i18n.properties`.
Use `SparkRes` class for accessing localized strings and images.

### Technology Stack
The project is written for Java 11 as a baseline.
Most of the XMPP logic is handled by the Smack library.
UI code is Swing-based and styled with FlatLaf. It uses some legacy SwingX components that should be avoided in new code.
Do not refactor the UI into a different framework.

## Build and Configuration

### Environment Requirements
- **JDK**: Java 11 or newer.
- **Maven**: 3.9.x or newer.

### Building and Test
Spark is a multi-module Maven project. To build the entire project from the root:
- Run a full build from repo root: `mvn clean verify`
- Run the main application from core: `cd core && mvn exec:java`
- Build and package only the `core`: `cd core && mvn clean verify`
- Run core tests: `cd core && mvn test`

## Testing
This is primarily a GUI project, so many changes may not need heavy unit testing.
However, add tests for any non-trivial logic, formatting helpers, data transformations, and bug fixes.
Run tests for related code changes and before committing after larger changes.

### Running Tests
Tests are primarily located in the `core` module. To run all tests in the `core` module:
```bash
mvn test -pl core
```

To run a specific test class:
```bash
mvn test -Dtest=JavaVersionTest -pl core
```

### Adding New Tests
- Place tests in the corresponding package under `core/src/test/java`.
- Use **JUnit 4** (the project's current testing framework).
- Example test structure:
```java
package org.jivesoftware.spark.util;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

public class MyNewTest {
    @Test
    public void testSomething() {
        assertTrue(true);
    }
}
```

## Code Style
Use Java 11 language features.
Prefer explicit types for public APIs and complex expressions.
Preserve existing package structure under `org.jivesoftware.spark` because it is an API used by plugins.
Plugin packages should be prefixed with `com.jivesoftware.spark.plugin` e.g. `package com.jivesoftware.spark.plugin.myplugin;`

### Code Formatting
The project has a legacy code with obsolete code style and formatting (ident with tabs, boilerplate, big methods, useless comments, use of `final` for local variables and parameters).
When changing an existing code reformat the method that is changed. Then gradually the code becomes easier to read and maintain.
If after formatting there was more than 40% of the class changed, then it is worth reformatting the whole class.
After that, commit the reformatting so in the commit history it would be easier to determine where it was reformat or refactoring and where it was functional changes.
Commit it with commit message `ClassName.methodName: reformat` or `ClassName: refactor` .
Use modern code formatting conventions but more AI-friendly.

Follow these formatting rules:
- Indent: 4.
- Brace style: same line.
- Reduce vertical noise.
- Blank lines: minimal. Don't put empty lines before a single line comment `//`.
- Avoid wildcard imports.
- Don't use `final` for local variables or parameters when they are effectively final. Remove the `final` when refactoring an existing code.
- Use consistent naming patterns everywhere.
  Example:
  ```
  find()
  get()
  load()
  create()
  update()
  delete()
  ```
  Avoid mixing:
  ```
  fetch()
  retrieve()
  lookup()
  obtain()
  ```
- Prefer composition over inheritance.
- Prefer early returns over else blocks.
- Flatten deep nesting.
- Prefer shorter identifiers (but still semantic).
- Add AI-friendly summary headers per file.
- Use predictable architecture patterns.
- Avoid using `var` in new code unless it clearly improves readability (long generics, see below).
- Remove unnecessary generics verbosity. 
  Bad:
  ```
  Map<String, List<UserDto>>
  ```
  Better (inside method):
  ```
  var users = new HashMap<String, List<UserDto>>();
  ```
- Remove redundant comments (prefer signal over narration). 
  Bad:
  ```
  // This method returns the user by id
  public User getUserById(String id)
  ```
  Good:
  ```
  public User findUser(String id)
  ```
  Don't use the `Optional` as a return type or a type of parameters: 
  ```
  public Optional<User> find(String id)
  ```
- Don't use `Optional`. If some library API returns it, then it should be converted immideatelly to a nullable vairable i.e. `var name = optionalName.orElse(null)`. 
- Avoid using streams with a long chain, big logic (try-catch blocks), calling actions that may fail with an exception. Use them only for a basic transform and filtering by properties. 
  Bad
  ```
  List<String> avatarUrls = contacts.stream()
      .filter((contact)-> contact.isAvailable())
      .map((contact)-> {
          try {
              return contact.getAvatarURL();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
      })
      .map(url -> url.toString())
      .collect(Collectors.toList());
  ```
  Better:
  ```
  List<String> avatarUrls = new ArrayList<>(contacts.size());
  for (var contact : contacts) {
      if (!contact.isAvailable()) {
          continue;
      }
      URL url;
      try {
          url = contact.getAvatarURL();
      } catch (Exception e) {
          throw new RuntimeException(e);
      }
      avatarUrls.add(url.toString());
  }
  ```
- Keep JavaDocs concise. If a method already has an obvious description, refine it and remove unnecessary `@param` and `@return` tags.
  Before:
  ```java
  /**
   * Gets the {@link PreferenceManager} instance.
   *
   * @return the PreferenceManager instance.
   */
  public static PreferenceManager getPreferenceManager() {
      return preferenceManager;
  }
  ```
  After:
  ```java
  /**
   * Get the {@link PreferenceManager} instance.
   */
  public static PreferenceManager getPreferenceManager() {
      return preferenceManager;
  }
  ```

#### Use SparkManager when possible
The `SparkManager` has many useful methods.
Use it to get global singletons and managers such as connection and MultiUserChatManager, etc.

Before:
```java
var mucManager = MultiUserChatManager.getInstanceFor(SparkManager.getConnection());
```
After:
```java
var mucManager = SparkManager.getMucManager();
```

### Logging
Use the `org.jivesoftware.spark.util.log.Log` class for logging:
```java
Log.error("The operation failed", e);
Log.debug("Debug message");
```

### Optimizations
Try to use more optimized code even if this may reduce readability.
If we have in the same method multiple calls to the same method that returns the same value
e.g. `SparkManager.getSessionManager()` then call it only once and save the result to a variable:

Before:
```java
SparkManager.getConnection().addAsyncStanzaListener(packetListener, presenceFilter);
SparkManager.getConnection().removeAsyncStanzaListener(packetListener);
```
After:
```java
Connection connection = SparkManager.getConnection();
connection.addAsyncStanzaListener(packetListener, presenceFilter);
connection.removeAsyncStanzaListener(packetListener);
```

## Plugin Development
Spark has a robust plugin system. Each plugin is located in the `plugins/` directory and contains its own `pom.xml` and `plugin.xml` metadata.
Refer to the [Sparkplug Development Guide](core/src/documentation/sparkplug_dev_guide.html) for more details.

Plugins should use their own Res class to load resources: translations and icons.
