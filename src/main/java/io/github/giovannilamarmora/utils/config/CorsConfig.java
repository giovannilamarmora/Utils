package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.utilities.FilesUtils;
import java.io.IOException;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CorsConfig implements Filter {
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Value(value = "#{new Boolean(${app.cors.enabled:false})}")
  private Boolean isCorsEnabled;

  @Value(value = "${app.shouldNotFilter}")
  private List<String> shouldNotFilter;

  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
      throws IOException, ServletException {

    if (isCorsEnabled || !shouldNotFilter(req)) {
      HttpServletResponse response = (HttpServletResponse) res;
      response.setHeader("Access-Control-Allow-Origin", "*");
      response.setHeader("Access-Control-Allow-Methods", "POST, PATCH, PUT, GET, OPTIONS, DELETE");
      response.setHeader("Access-Control-Max-Age", "3600");
      response.setHeader("Access-Control-Expose-Headers", "*");
      response.setHeader(
          "Access-Control-Allow-Headers",
          "Origin, X-Requested-With, Content-Type, Accept, Authorization");
      LOG.info("Setting Up CORS Policy for mainstream: {}", response);
    }
    chain.doFilter(req, res);
  }

  private boolean shouldNotFilter(ServletRequest req) {
    HttpServletRequest request = (HttpServletRequest) req;
    String path = request.getRequestURI();
    return shouldNotFilter.stream().anyMatch(endpoint -> FilesUtils.matchPath(path, endpoint));
  }
}
