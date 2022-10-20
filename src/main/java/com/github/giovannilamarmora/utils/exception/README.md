# UtilsException

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

How to work with a centralized Exception

First of all you need to enable it into your code, goes into the Application started and Import the UtilsException.

```
@SpringBootApplication
@Import(UtilsException.class)
public class ProjectApplication {

  public static void main(String[] args) {
    SpringApplication.run(TmsProjectApplication.class, args);
  }
}

```

On your code you need to map the error in an ExceptionMapper(You need to create it), it could be like this:

```
public enum TestException implements ExceptionCode {
  ERRCODPRJ001("TEST_EXCEPTION", HttpStatus.INTERNAL_SERVER_ERROR, "Message"),
  ERRCODPRJ002("TEST_EXCEPTION2", HttpStatus.INTERNAL_SERVER_ERROR, "Message");

  private final HttpStatus status;
  private final String message;
  private final String exceptionName;

  TestException(String exceptionName, HttpStatus status, String message) {
    this.exceptionName = exceptionName;
    this.status = status;
    this.message = message;
  }

  @Override
  public String exceptionName() {
    return exceptionName;
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

And it will return an Object like this:

```
{
    "dateTime": "2022-10-19T16:34:14",
    "url": "/import/current_account",
    "exceptionCode": "ERRDEFUTL001",
    "exceptionName": "MissingServletRequestParameterException",
    "status": "BAD_REQUEST",
    "correlationId": "0ed170ee-a78c-40e4-b456-f17aa7ec7db9",
    "message": "Exception Message: Required request parameter 'test' for method parameter type String is not present"
}
```

## Handle New Exception

To handle a new exception the code would be like this:

```
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
    if (e != null) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      return new ResponseEntity<>(getExceptionResponse(e, request, null, status), status);
    } else {
      return new ResponseEntity<>(error, DEFAULT_STATUS);
    }
  }
}
```