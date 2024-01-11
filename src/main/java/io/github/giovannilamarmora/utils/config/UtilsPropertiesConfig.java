package io.github.giovannilamarmora.utils.config;

import javax.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UtilsPropertiesConfig {

  private static final Logger LOG = LoggerFactory.getLogger(UtilsPropertiesManager.class);
  @Autowired private UtilsPropertiesManager propertiesManager;
  private final UtilsPropertiesManager target;

  @PostConstruct
  private void init() {
    LOG.info("Set Bean for {}", propertiesManager);
    BeanUtils.copyProperties(propertiesManager, this.target);
  }
}
