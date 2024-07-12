# Context

_go to_ `Utils Project` 🚀 ([Readme.me](../../../../../../../../README.md) 📄)

<hr>

# Distributed Tracing Utility for Spring WebFlux

This project provides a distributed tracing implementation for Spring WebFlux applications. It offers functionality to
generate and propagate tracing identifiers (Trace-ID, Span-ID, Parent-ID) across HTTP requests and logging context.

## Features

- Automatic generation of Trace-ID, Span-ID, and Parent-ID for each request
- Propagation of IDs through HTTP headers
- Integration with SLF4J MDC (Mapped Diagnostic Context) for logging
- Support for context propagation in reactive environments
- Environment (env) configuration via Spring properties

## Main Components

### TracingFilter

`TracingFilter` is a Spring `WebFilter` that intercepts incoming HTTP requests and:

1. Extracts or generates tracing IDs (Trace-ID, Span-ID, Parent-ID)
2. Adds these IDs to the HTTP response headers
3. Populates the MDC with tracing IDs and environment
4. Propagates the tracing context through the reactive filter chain

### ContextRegistry

`ContextRegistry` is responsible for initializing the context propagation registry. It:

1. Enables automatic context propagation for Reactor
2. Registers MDC keys for Trace-ID, Span-ID, Parent-ID, and environment

### TraceUtils

`TraceUtils` provides utility methods to:

1. Generate new tracing IDs
2. Retrieve current tracing IDs from MDC
3. Handle a thread-specific span ID

## Usage

To use this library in your Spring WebFlux project:

1. Ensure that `TracingFilter`, `ContextRegistry`, and `TraceUtils` classes are in the classpath and scanned by Spring.
2. Configure the `env` property in your Spring configuration file (e.g., `application.properties` or `application.yml`).
3. Use a logger configured to include tracing IDs in logs.

## Configuration

Example configuration in `application.properties`:

```properties
env=production
```

## Logging

Ensure your logger configuration (e.g., Logback) includes MDC fields. Example for Logback:

```xml

<pattern>%d{ISO8601} [%thread] %-5level %logger{36} [%X{Trace-ID}] [%X{Span-ID}] [%X{Parent-ID}] [%X{env}] - %msg%n
</pattern>
```