# MathService

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

---

The `MathService` class in the `io.github.giovannilamarmora.utils.math` package provides mathematical utility methods,
particularly focused on rounding decimal values.

## Class Overview 🧮

- **Package:** io.github.giovannilamarmora.utils.math
- **Service:** Annotated with `@Service` to indicate it as a Spring service component.
- **Logging:** Integrated with logging using SLF4J, with logs at the class level.

## Methods 📊

### `round`

This method is used to round a given decimal value to a specified number of decimal places.

#### Method Signature:

- `public static double round(double value, int places) throws UtilsException`

#### Parameters:

- `value`: The decimal value to be rounded.
- `places`: The number of decimal places.

#### Returns:

- Returns the rounded decimal value.

#### Exceptions:

- Throws a `UtilsException` if the specified number of decimal places is less than 0.

## Logging and Interceptors 📝

- The class is annotated with `@Logged`, indicating integration with a broader logging framework.
- Specific methods are annotated with `@LogInterceptor` to log the execution time using a custom
  interceptor (`LogTimeTracker`).

## Notes 📝

- Ensure proper error handling when using these mathematical utilities in real-world scenarios.
- Adapt the class as needed based on specific mathematical requirements.
- Utilize the provided documentation for clarity on method usage.

Feel free to leverage these utility methods in your project for mathematical operations.

```
double round(double value, int places)
```

This method is used to drop decimal values. Value double is passed with the number of decimal as places.