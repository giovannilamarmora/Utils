# Interceptors

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

<hr>

```
@LogInterceptor(actionType=APP_ENDPOINT)
```

The logger Interceptor is used to get the full LOG of the method called;

To be used you need to add a dependency (Try first without it):

```xml

<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.7</version>
</dependency>
```

And then create a class named AppConfig

```java

@ComponentScan(basePackages = "com.github.giovannilamarmora.utils")
@Configuration
@EnableAspectJAutoProxy
public class AppConfig {
}
```