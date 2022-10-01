# UtilsException

How to work with a centralized Exception

On your code you need to map the error in an ExceptionMapper(You need to create it), it could be like this:

```
public enum TestException implements ExceptionCode {
  TEST_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "Message"),
  TEST_EXCEPTION1(HttpStatus.INTERNAL_SERVER_ERROR, "Message");

  private final HttpStatus status;
  private final String message;

  TestException(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
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
    "error": "TEST_EXCEPTION",
    "status": "INTERNAL_SERVER_ERROR",
    "correlationId": "a910574d-d7b8-4467-86e6-215659b8dbcb",
    "message": "Message | Exception Message:  (Impossibile trovare il percorso specificato)"
}
```