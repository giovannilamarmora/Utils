package io.github.giovannilamarmora.utils.config;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorsConfig implements Filter {

  @Value(value = "#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    if (isCorsEnabled) {
      HttpServletResponse response = (HttpServletResponse) res;
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Access-Control-Allow-Methods", "POST, PATCH, PUT, GET, OPTIONS, DELETE");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Expose-Headers", "*");
      response.setHeader(
          "Access-Control-Allow-Headers",
          "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      LOG.info("Setting Up CORS Policy for mainstream: {}", response);
      chain.doFilter(req, res);
    }
  }
}
