package io.github.giovannilamarmora.utils.interceptors.correlationID;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorrelationIdInterceptor extends OncePerRequestFilter {

  private static boolean isEmpty(String value) {
    return value == null || value.isBlank();
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = request.getHeader(CorrelationIdUtils.CORRELATION_HEADER_NAME);
    String mdcCorrelationId = MDC.get(CorrelationIdUtils.CORRELATION_MDC_NAME);
    if (isEmpty(correlationId) || !correlationId.equalsIgnoreCase(mdcCorrelationId)) {
      MDC.remove(CorrelationIdUtils.CORRELATION_MDC_NAME);
      correlationId = CorrelationIdUtils.generateCorrelationId();
    }
    CorrelationIdUtils.setCorrelationId(correlationId);
    MDC.put(CorrelationIdUtils.CORRELATION_MDC_NAME, CorrelationIdUtils.getCorrelationId());
    response.addHeader(
        CorrelationIdUtils.CORRELATION_HEADER_NAME, CorrelationIdUtils.getCorrelationId());
    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return "/actuator/health".equals(path);
  }
}
