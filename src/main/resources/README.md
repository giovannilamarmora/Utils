# Application Configuration Documentation 🛠️

This document provides an overview of the key variables in the `application.yml` file, explaining their purpose and
usage within the application.

```yml
app:
  exception:
    stacktrace:
      utilsException:
        active: true # Active StackTrace in Response
        debug: false # If active: true and debug: false activate the  StackTrace in LOGS
  # Cors status
  cors:
    enabled: false

  # Level of LOG
logging:
  config: classpath:logback-spring.xml # classpath:logback-google.xml, classpath:logback-logtail.xml
```

## `app` Section

### `exception` Subsection

#### `stacktrace` Sub-Subsection

- `utilsException` Sub-Sub-Subsection:

    - `active`: Determines whether the application should include the stack trace in the response when encountering
      a `UtilsException`.

    - `debug`: If `active` is set to `true`, controls whether the stack trace should also be logged in the application
      logs.

### `cors` Subsection

- `enabled`: Controls whether Cross-Origin Resource Sharing (CORS) is enabled. If set to `true`, CORS is enabled; if set
  to `false`, CORS is disabled.

## Notes 📝

- Modify these configurations based on your application's requirements and environment.
- Ensure sensitive information is not exposed or logged inappropriately, especially when enabling stack traces in
  responses or logs.

Feel free to adjust these configurations to meet the specific needs of your application and environment.

---

# Logging Configuration Documentation

This section provides an overview of the logging configuration in the `application.yml` file, detailing the specified
logging levels for various packages within the application.

```yml
# Level of LOG
logging:
  level:
    io.github.giovannilamarmora.utils: INFO
    web: ERROR
    root: INFO
    org:
      springframework:
        web: ERROR
      hibernate: ERROR
```

## `logging` Section

### `level` Subsection

- `io.github.giovannilamarmora.utils`: Sets the logging level for classes within the `io.github.giovannilamarmora.utils`
  package to INFO.

- `web`: Sets the logging level for classes within the `web` package to ERROR.

- `root`: Sets the root logging level to INFO.

#### `org` Sub-Subsection

- `springframework` Sub-Sub-Subsection:

    - `web`: Sets the logging level for classes within the `org.springframework.web` package to ERROR.

- `hibernate` Sub-Sub-Subsection:

    - Sets the logging level for classes within the `org.hibernate` package to ERROR.

## Notes 📝

- Logging levels control the verbosity of log output. Adjust these levels based on the desired level of detail in your
  application logs.
- Ensure that sensitive information is not exposed or logged inappropriately.
- Review and update logging configurations based on the specific needs and requirements of your application and
  environment.

---

# Management Endpoints Configuration Documentation

This section provides an overview of the configuration related to management endpoints in the `application.yml` file. It
includes settings for information, environment, health probes, and web endpoints exposure.

```yml
management:
  info:
    enabled: true
  env:
    enable: true
  health:
    probes:
      enabled: true
  endpoints:
    web:
      exposure:
        include: health,info,metrics,beans,loggers
```

## `management` Section

### `info` Subsection

- `enabled`: Controls whether the information endpoint is enabled. If set to `true`, the information endpoint is active;
  if set to `false`, the information endpoint is disabled.

### `env` Subsection

- `enable`: Controls whether the environment endpoint is enabled. If set to `true`, the environment endpoint is active;
  if set to `false`, the environment endpoint is disabled.

### `health` Subsection

- `probes` Sub-Subsection:

    - `enabled`: Controls whether health probes are enabled. If set to `true`, health probes are active; if set
      to `false`, health probes are disabled.

### `endpoints` Subsection

#### `web` Sub-Subsection

- `exposure` Sub-Sub-Subsection:

    - `include`: Specifies the endpoints to be exposed via the web. In this configuration, the following endpoints are
      included: health, info, metrics, beans, loggers.

## Notes 📝

- Management endpoints provide useful information about the application's health, environment, and other runtime
  details.
- Enable or disable specific endpoints based on security and information disclosure considerations.
- Adjust the `include` list to expose additional or fewer endpoints as needed.
- Keep sensitive information secure and avoid exposing unnecessary details in production environments.

---

# Logback Configuration Documentation

This XML configuration file provides the Logback setup for logging in the application. Logback is a logging framework
for Java applications.

## Configuration Overview 📋

- **`logTailActive`**: A property that determines whether logging to LogTail is active.

### Appenders 📤

- **`LogtailHttp`**: A Logtail appender responsible for sending logs to LogTail. It includes a specific pattern for log
  messages, MDC (Mapped Diagnostic Context) fields, and an app name and ingest key.

- **`Logtail`**: An asynchronous appender that includes the `LogtailHttp` appender as a reference. It sets the queue
  size, discarding threshold, and includes caller data in the logs.

- **`Console`**: A console appender for logging to the console. It includes a pattern with timestamp, log level, thread,
  class name, and log message.

### Root Logger Configuration 🌐

- The root logger is configured based on the value of `logging.config`.

    - If `logging.config` is `classpath:logback-logtail.xml`, the root logger includes both the `Logtail` and `Console`
      appenders, with the
      logging level specified by the `level` property.

### Notes 📝

- Adjust the configuration based on the specific logging requirements of your application.
- Be cautious with sensitive information in logs, especially when using external log services like LogTail.
- The provided configuration uses properties to make it more dynamic and customizable.

---

# Google Cloud Logging Configuration

## Introduction📝

Google Cloud Logging allows you to store, search, analyze, and monitor your logs generated from your applications and
infrastructure on Google Cloud Platform. This document provides guidance on configuring and using Google Cloud Logging
with Logback in a Java application.

## Prerequisites📤

Before you begin, ensure you have the following:

A Google Cloud Platform (GCP) account.
A Java application using Logback for logging.
Basic familiarity with Google Cloud Platform and Java.
Configuration

1. Set Up Google Cloud Project <br>
   Log in to the Google Cloud Console. <br>
   Create a new project or select an existing project. <br>
   Enable the Cloud Logging API for your project.
2. Add Dependencies <br>
   Ensure your Java project includes the necessary dependencies:
    ```xml
    <!-- Google Cloud Logging -->
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-gcp-starter-logging</artifactId>
        <version>1.2.8.RELEASE</version>
    </dependency>
    <dependency>
        <groupId>com.google.api-client</groupId>
        <artifactId>google-api-client</artifactId>
        <version>2.4.1</version>
    </dependency>
    ```
3. Configure Logback <br>
   Modify your Logback configuration file (e.g., logback.xml) to include an appender for Google Cloud Logging:

    ```xml
    <appender name="CLOUD" class="com.google.cloud.logging.logback.LoggingAppender">
    <!-- Add configuration for Google Cloud Logging appender -->
    </appender>
    
    <root level="INFO">
        <appender-ref ref="CLOUD"/>
    </root>
    ```
4. Enhance Logging Events <br>
   Optionally, enhance your logging events with additional information before they are sent to Google Cloud Logging. You
   can implement a LoggingEventEnhancer class:

    ```java
    public class GoogleLogConfig implements LoggingEventEnhancer {
    // Implement the enhanceLogEntry method to add custom fields
    }
    ```

### Usage

Once configured, your application will start logging to Google Cloud Logging automatically. You can view and analyze
your logs using the Google Cloud Console.

### Root Logger Configuration 🌐

- The root logger is configured based on the value of `logging.config`.

    - If `logging.config` is `classpath:logback-google.xml`, the root logger includes both the `Logtail` and `Console`
      appenders.

### Troubleshooting

Verify that the Cloud Logging API is enabled for your project.
Ensure that the necessary dependencies are added to your project.
Check your Logback configuration for any errors or misconfigurations.
