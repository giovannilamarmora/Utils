# UtilsException

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

How to work with a centralized Exception

## Enable UtilsException

### 1. Via Starter Application

First of all you need to enable it into your code, goes into the Application started and Import the UtilsException.

```java

@SpringBootApplication
@Import(UtilsException.class)
public class ProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmsProjectApplication.class, args);
    }
}
```

### 2. Via AppConfig

If you already enabled **Log Interceptor**
📈 ([Readme.me](src/main/java/com/github/giovannilamarmora/utils/interceptors/README.md) 📄) and created the AppConfig (
Or whatever name you choose for it) you already have the exception interceptor enabled on your project, if not working,
goes to step 1, else you can create the AppConfig class like this:

```java

@ComponentScan(basePackages = "com.github.giovannilamarmora.utils")
@Configuration
public class AppConfig {
}
```

<hr>

## Use UtilsException

On your code you need to map the error in an ExceptionMapper(You need to create it), it could be like this:

```java
public enum TestException implements ExceptionCode {
    ERRCODPRJ001("TEST_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR, "Message"),
    ERRCODPRJ002("TEST_EXCEPTION2", HttpStatus.INTERNAL_SERVER_ERROR, "Message");

    private final HttpStatus status;
    private final String message;
    private final String exceptionName;

    TestException(String exceptionName, HttpStatus status, String message) {
        this.exceptionName = exception;
        this.status = status;
        this.message = message;
    }

    @Override
    public String exception() {
        return exception;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }
}
```

Then on your method, whenever you need to throw an Exception could be like this:

```
try {
    // Write your code
} catch (Exception e) {
  throw new UtilsException(TestException.TEST_EXCEPTION, e.getMessage());
```

Or create a new class:

```java
public class ImageException extends UtilsException {

    private static final ExceptionCode DEFAULT_CODE = TestException.ERR_IMG_MSS_001;

    public ImageException(String message) {
        super(DEFAULT_CODE, message);
    }

    public ImageException(String message, String exceptionMessage) {
        super(DEFAULT_CODE, message, exceptionMessage);
    }
}
```

And it will be like this:

```
try {
    // Write your code
} catch (Exception e) {
  throw new ImageException(e.getMessage());
```

And it will return an Object like this:

```json
{
  "dateTime": "2024-01-10T22:49:03",
  "url": "/v1/exception",
  "correlationId": "ce7ee35d-a4cc-4806-a570-e7bb20328a04",
  "error": {
    "errorCode": "ERR_CODE_UTL_001",
    "exception": "GENERIC_EXCEPTION",
    "status": "UNAUTHORIZED",
    "exceptionMessage": "message",
    "stackTrace": "[stacktrace]"
  }
}
```

To remove the stacktrace from the UtilsException add this on your application.yml

```yml
app:
  exception:
    stacktrace:
      utilsException: false
```

## Handle New Exception

To handle a new exception the code would be like this:

```java

@ControllerAdvice
public class ExceptionHandler extends UtilsException {

    @org.springframework.web.bind.annotation.ExceptionHandler(
            value = MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        LOG.error(
                "An error happened while calling {} Downstream API: {}",
                request.getRequestURI(),
                e.getMessage());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(getExceptionResponse(e, request, null, status), status);
    }
}
```
