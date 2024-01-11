package io.github.giovannilamarmora.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilsConfig {

  @Bean
  public UtilsPropertiesManager propertiesManager() {
    return new UtilsPropertiesManager();
  }
}
