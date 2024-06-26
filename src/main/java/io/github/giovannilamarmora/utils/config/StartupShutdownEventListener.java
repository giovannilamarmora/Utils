package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Logged
public class StartupShutdownEventListener {
  private static final Logger LOG = LoggerFilter.getLogger(StartupShutdownEventListener.class);
  private static final String APPLICATION_NAME = "spring.application.name";
  private static final String APPLICATION_NAME_DEFAULT = "Application";
  private static final String STARTUP_LOG_ACTIVE = "spring.application.starter-log";
  private static final String SERVER_SSL_KEY = "server.ssl.key-store";
  private static final String SERVER_PORT = "server.port";
  private static final String SERVER_CONTEXT_PATH = "server.servlet.context-path";

  @Value("${app.version:unknown}")
  private String appVersion;

  private static void logApplicationStartup(Environment env, String appVersion) {
    String protocol =
        Optional.ofNullable(env.getProperty(SERVER_SSL_KEY)).map(key -> "https").orElse("http");
    String serverPort = Optional.ofNullable(env.getProperty(SERVER_PORT)).orElse("8080");
    String contextPath =
        Optional.ofNullable(env.getProperty(SERVER_CONTEXT_PATH))
            .filter(StringUtils::isNotBlank)
            .orElse("/");
    String localAddress = "localhost";
    String hostAddress = "localhost";
    try {
      hostAddress = InetAddress.getLocalHost().getHostAddress();
      localAddress = InetAddress.getLocalHost().getHostName();
    } catch (UnknownHostException e) {
      LOG.warn("The host name could not be determined, using `localhost` as fallback", e);
    }
    LOG.info(
        "\n--------------------------------------------------------------------\n\t"
            + "Application '{}' is running! Access URLs:\n\t"
            + "Local: \t\t{}://{}:{}{}\n\t"
            + "External: \t{}://{}:{}{}\n\t"
            + "Profile(s): \t{}\n\t"
            + "App Version: \t[{}]\n--------------------------------------------------------------------",
        env.getProperty(APPLICATION_NAME, APPLICATION_NAME_DEFAULT),
        protocol,
        localAddress,
        serverPort,
        contextPath,
        protocol,
        hostAddress,
        serverPort,
        contextPath,
        env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles(),
        appVersion);
  }

  @EventListener
  private void onStartup(ApplicationReadyEvent event) {
    Environment environment = event.getApplicationContext().getEnvironment();
    LOG.debug(
        "Startup Application Logging Started for {}",
        environment.getProperty(APPLICATION_NAME, APPLICATION_NAME_DEFAULT));
    boolean isActive = Boolean.parseBoolean(environment.getProperty(STARTUP_LOG_ACTIVE, "false"));
    if (!isActive) {
      LOG.debug(
          "Skipped Startup Application Logging, to enable it add the propriety {} with value true",
          STARTUP_LOG_ACTIVE);
      return;
    }
    logApplicationStartup(environment, appVersion);
  }
}
