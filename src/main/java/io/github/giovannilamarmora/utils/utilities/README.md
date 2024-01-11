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
