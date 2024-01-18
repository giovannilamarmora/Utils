package io.github.giovannilamarmora.utils.web;

import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.text.StringTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Logged
public class WebManager {

  private static final Logger LOG = LoggerFactory.getLogger(WebManager.class);
  private static final String CLIENT_IP = "Client-IP";
  private static final String X_FORWARDED_FOR = "X-Forwarded-For";
  private static final String X_ORIGINAL_FORWARDED_FOR = "x-original-forwarded-for";

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public String getRealClientIP(HttpServletRequest request) {
    LOG.debug("Getting Real Client IP for {}", request.getRequestURI());
    String ipClient = null;
    String headerClientIp = request.getHeader(CLIENT_IP);
    String headerXForwardedFor = request.getHeader(X_FORWARDED_FOR);
    String headerOriginalXForwardedFor = request.getHeader(X_ORIGINAL_FORWARDED_FOR);
    LOG.info("Info header Client-IP: " + headerClientIp);
    LOG.info("Info header X-Forwarded-For: " + headerXForwardedFor);
    LOG.info("Info header x-original-forwarded-for: " + headerOriginalXForwardedFor);

    if (headerClientIp != null && !headerClientIp.isEmpty()) {
      ipClient = headerClientIp;
    } else if (headerXForwardedFor != null && !headerXForwardedFor.isEmpty()) {
      ipClient = headerXForwardedFor;
    } else if (headerOriginalXForwardedFor != null && !headerOriginalXForwardedFor.isEmpty()) {
      ipClient = getClientIp(request, headerOriginalXForwardedFor);
    }
    if (ipClient == null) {
      ipClient = request.getRemoteAddr();
    }
    LOG.debug("Ended Get Real Client IP: {}", ipClient);
    return ipClient;
  }

  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public String getClientIp(HttpServletRequest request, String xForwardedForHeader) {
    LOG.debug("Getting Client IP for {}", xForwardedForHeader);
    if (xForwardedForHeader == null) {
      return request.getRemoteAddr();
    } else {
      return new StringTokenizer(xForwardedForHeader, ",").nextToken().trim();
    }
  }
}
