# Web Utils 🌐

_go to_ [Utils Project](../../../../../../../../README.md) 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

---

## ThreadManager 🧵

### Functionality 💡

#### `threadSeep(Integer millisecond)`

This method is annotated with `@LogInterceptor` to log the execution time of the method using a `LogTimeTracker`. It
sleeps the current thread for the specified number of milliseconds.

- **Parameters:**
    - `millisecond` (Integer): The duration in milliseconds for which the thread should sleep.

- **Logging:**
    - Logs a debug message indicating the start of the process.
    - Logs an info message indicating that the thread is sleeping for the specified duration.
    - Logs an error message and throws a `WebException` if an `InterruptedException` occurs during the sleep.

### Interceptors and Logging 📝

#### `@Logged`

The class is annotated with `@Logged`, suggesting that it is part of a broader logging framework. This annotation could
be used for cross-cutting concerns, such as logging method entry and exit points.

#### `@LogInterceptor`

The `@LogInterceptor` annotation on the `threadSeep` method indicates the use of a custom interceptor (`LogTimeTracker`)
to log the execution time of the method.

### Exception Handling ⚠️

In case of an `InterruptedException` during the sleep, the method logs an error message, throws a `WebException`, and
includes an error code (`GenericException.ERR_EXC_WEB_001`) and a descriptive error message.

### Logging 🪵

The class uses the SLF4J logging framework with a logger named `LOG`. It logs messages at different
levels (`DEBUG`, `INFO`, `ERROR`) depending on the context.

### Usage 🚀

This class can be injected into other components of a Spring application to provide thread-related utility
functionality. The `threadSeep` method, in particular, can be useful for introducing delays in a controlled manner.

```java

@Service
public class MyService {

    private final ThreadManager threadManager;

    @Autowired
    public MyService(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    public void performTaskWithDelay() {
        // Perform some tasks before the delay
        // ...

        // Use ThreadManager to introduce a delay
        threadManager.threadSeep(5000); // Sleep for 5 seconds

        // Continue with other tasks after the delay
        // ...
    }
}
```

---

## CookieManager 🍪

### Functionality 💡

#### `setCookieInResponse(String cookieName, String cookieValue, HttpServletResponse response)`

This method is annotated with `@LogInterceptor` to log the execution time of the method using a `LogTimeTracker`. It
sets a cookie with the specified name and value in the HTTP response.

- **Parameters:**
    - `cookieName` (String): The name of the cookie.
    - `cookieValue` (String): The value of the cookie.
    - `response` (HttpServletResponse): The HTTP response object.

- **Logging:**
    - Logs an info message indicating the setting of the cookie with its name and value.
    - Logs a debug message indicating the successful setting of the cookie in the HTTP response.

### Interceptors and Logging 📝

#### `@Logged`

The class is annotated with `@Logged`, suggesting that it is part of a broader logging framework. This annotation could
be used for cross-cutting concerns, such as logging method entry and exit points.

#### `@LogInterceptor`

The `@LogInterceptor` annotation on the `setCookieInResponse` method indicates the use of a custom
interceptor (`LogTimeTracker`) to log the execution time of the method.

### Logging 🪵

The class uses the SLF4J logging framework with a logger named `LOG`. It logs messages at different
levels (`DEBUG`, `INFO`, `ERROR`) depending on the context.

### Usage 🚀

This class can be injected into other components of a Spring application to provide cookie-related utility
functionality. The `setCookieInResponse` method is designed to set cookies in HTTP responses.

```java
import io.github.giovannilamarmora.utils.web.CookieManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/example")
public class ExampleController {

    @Autowired
    private CookieManager cookieManager;

    @GetMapping("/setCookie")
    public String setCookie(HttpServletResponse response) {
        // Set a cookie named "userToken" with the value "abc123" in the HTTP response
        cookieManager.setCookieInResponse("userToken", "abc123", response);
        CookieManager.setCookieInResponse("userToken", "abc123", response);

        return "Cookie set successfully!";
    }
}
```