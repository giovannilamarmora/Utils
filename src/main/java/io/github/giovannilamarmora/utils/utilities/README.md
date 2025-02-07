# Utilities

_go to_ [Utils Project](../../../../../../../../README.md) 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

---

## Overview 🌐

The `Utilities` class provides a set of utility methods for common operations, including object-to-json conversion,
map-to-string conversion, and fetching base64-encoded image data from a URL. The class is designed to be a part of a
larger utility library.

### Features ✨

- Object-to-json conversion.
- Map-to-string conversion.
- Fetching base64-encoded image data from a URL.

### Interceptors and Logging 📝

The class is annotated with `@Logged`, indicating its integration with a broader logging framework. Additionally,
specific methods are annotated with `@LogInterceptor` to log the execution time using a custom
interceptor (`LogTimeTracker`).

### Dependencies 🛠️

- Spring Framework
- SLF4J Logging
- Jackson ObjectMapper

---

## Usage 🚀

### 1. Object-to-json Conversion

```java
import io.github.giovannilamarmora.utils.utilities.Utilities;

public class Example {

    public static void main(String[] args) {
        MyObject myObject = new MyObject("John", 25);
        try {
            String json = Utilities.convertObjectToJson(myObject);
            // Handle the json string as needed
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
```

### 2. Map-to-string Conversion

```java
import io.github.giovannilamarmora.utils.utilities.Utilities;

import java.util.HashMap;
import java.util.Map;

public class Example {

    public static void main(String[] args) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put("name", "John");
        myMap.put("age", 25);

        String mapAsString = Utilities.convertMapToString(myMap);
        // Handle the mapAsString as needed
    }
}

```

### 3. Fetching Base64-encoded Image Data from URL

```java
import io.github.giovannilamarmora.utils.utilities.Utilities;

public class Example {

    public static void main(String[] args) {
        String imageUrl = "https://example.com/image.png";
        String base64ImageData = Utilities.getByteArrayFromImageURL(imageUrl);
        // Handle the base64ImageData as needed
    }
}
```

## Notes 📝

- Ensure proper error handling and null-checking in a real-world scenario.
- Make sure to handle potential exceptions thrown by the utility methods.

Feel free to explore and integrate these utility methods into your project for enhanced functionality!

---

# ObjectToolkit

## Overview 🌐

The `ObjectToolkit` class provides a variety of utility methods designed to simplify common operations like handling
null or empty checks, deserialization, working with maps, and more. These methods allow you to avoid repetitive
boilerplate code, ensuring that your code is more concise, readable, and maintainable.

### Features ✨

- Placeholder replacement in maps.
- Null or empty checks for objects, collections, and nested properties.
- Easy deserialization and validation of objects.
- Enum validation.
- Safe property retrieval with fallback values.

### Interceptors and Logging 📝

The class is annotated with `@LogInterceptor` to integrate logging functionalities and track execution times using
`LogTimeTracker`.

### Dependencies 🛠️

- Spring Framework
- Jackson ObjectMapper
- Reflection API

---

## Methods & Usage 🚀

### 1. Placeholder Replacement in Maps

#### Description 📝

This method performs a placeholder replacement in the values of a map. Given a `target` map with values containing
placeholders in the format `{{key}}`, it replaces them with the corresponding values from the `source` map.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

import java.util.Map;

public class Example {
    public static void main(String[] args) {
        Map<String, String> source = Map.of("name", "John", "age", "30");
        Map<String, String> target = Map.of("user", "{{name}}", "age", "{{age}} years old");

        Map<String, String> result = ObjectToolkit.getFinalMapFromValue(source, target);
        System.out.println(result);
        // Result: {user=John, age=30 years old}
    }
}

```

### 2. Check if Fields are Null or Empty

#### Description 📝

This method checks if all fields of the given object are null or empty. It uses reflection to access the object's fields
and returns true if all fields are either null or empty, otherwise it returns false.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

public class Example {
    public static void main(String[] args) {
        class User {
            private String name;
            private String email;

            // Constructor, getters, setters
        }

        User user = new User();
        boolean isNullOrEmpty = ObjectToolkit.fieldsNullOrEmpty(user);
        System.out.println(isNullOrEmpty);
        // Result: true (because both fields are null)
    }
}

```

### 3. Deserialization and Validation

#### Description 📝

This method attempts to deserialize a JSON string (source) into an object of a given type (typeReference) and checks if
the resulting object is not empty. It's useful for validating whether a JSON string can be deserialized into a
meaningful object.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import com.fasterxml.jackson.core.type.TypeReference;

public class Example {

    public static void main(String[] args) {
        String json = "{\"name\": \"John\", \"age\": 30}";
        boolean isValid = ObjectToolkit.isInstanceOf(json, new TypeReference<User>() {
        });
        // Result: true (if the object is deserialized and not empty)
    }
}

```

### 4. Null or Empty Check for an Object

#### Description 📝

This method checks if the provided object is null or empty. It supports various types such as Optional, String,
Collection, Map, and Arrays, returning true if the object is empty or null.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

public class Example {

    public static void main(String[] args) {
        String str = "";
        boolean isEmpty = ObjectToolkit.isNullOrEmpty(str);
        // Result: true (because the string is empty)
    }
}
```

### 5. Enum Validation

#### Description 📝

This method checks if a given string is a valid constant of the specified enum class. It returns true if the string
corresponds to a valid enum constant, otherwise false.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

public class Example {

    public static void main(String[] args) {
        enum Status {ACTIVE, INACTIVE}

        boolean isValid = ObjectToolkit.isEnumValue("ACTIVE", Status.class);
        // Result: true (because "ACTIVE" is a valid enum constant)
    }
}
```

### 6. Safe Property Retrieval with Null Check

#### Description 📝

This method retrieves the value of a property from an object if it's not null or empty; otherwise, it returns null. It's
useful for safely getting values from potentially null properties.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

public class Example {

    public static void main(String[] args) {
        User user = new User("John", "john@example.com");
        String email = ObjectToolkit.getValueOrNull(user, User::getEmail);
        // Result: "john@example.com" if it's not null or empty, otherwise null
    }
}
```

### 7. Safe Property Retrieval with Default Value

#### Description 📝

This method retrieves the value of a property from an object if it's not null or empty; otherwise, it returns a default
value. It's useful for providing fallback values when the property is empty or null.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;

public class Example {

    public static void main(String[] args) {
        User user = new User("John", null);
        String email = ObjectToolkit.getValueOrDefault(user, User::getEmail, "default@example.com");
        // Result: "default@example.com" because the email is null
    }
}
```

### 8. Nested Property Check for Null or Empty

#### Description 📝

This method checks a series of nested properties to ensure all values are neither null nor empty. It helps you safely
navigate and validate nested fields in an object chain.

#### Example 💡

```java
import io.github.giovannilamarmora.utils.generic.Response;
import io.github.giovannilamarmora.utils.utilities.ObjectToolkit;
import org.springframework.http.ResponseEntity;

public class Example {

    public static void main(String[] args) {
        boolean isValid = ObjectToolkit.areNotNullOrEmptyCast(responseEntityWallet,
                (ResponseEntity<Response> re) -> re.getBody(),
                (Response body) -> body.getData());
        boolean isValid = ObjectToolkit.areNotNullOrEmpty(responseEntityWallet,
                ObjectToolkit.lift(ResponseEntity::getBody),
                ObjectToolkit.lift(Response::getData));
        // Result: true if both responseEntityWallet.getBody() and responseEntityWallet.getBody().getData() are not null or empty
    }
}
```

## Notes 📝

- These utility methods handle various data types, including `Optional`, `String`, `Collection`, `Map`, and Arrays.
- Reflection is used for accessing object fields, so be mindful of performance when dealing with large or deeply nested
  objects.
- The methods are designed to prevent redundant null and empty checks, simplifying your codebase.
- Ensure that the getter functions used in methods like `areNotNullOrEmpty` are correctly defined.

Feel free to integrate these utility methods into your project for enhanced functionality and cleaner code! 😎
