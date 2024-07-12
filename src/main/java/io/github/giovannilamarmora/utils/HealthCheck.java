package io.github.giovannilamarmora.utils;

import io.github.giovannilamarmora.utils.context.TraceUtils;
import io.github.giovannilamarmora.utils.generic.Response;
import io.github.giovannilamarmora.utils.interceptors.LogInterceptor;
import io.github.giovannilamarmora.utils.interceptors.LogTimeTracker;
import io.github.giovannilamarmora.utils.interceptors.Logged;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Logged
@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*")
public class HealthCheck {
  @GetMapping(value = "/health-check", produces = MediaType.APPLICATION_JSON_VALUE)
  @Tag(name = "Health Check", description = "API to check if running")
  @Operation(description = "API to check if running", tags = "Health Check")
  @LogInterceptor(type = LogTimeTracker.ActionType.UTILS_LOGGER)
  public ResponseEntity<Response> healthCheck() {
    return ResponseEntity.ok(
        new Response(
            HttpStatus.OK.value(),
            LocalDateTime.now() + " the system is ACTIVE.",
            TraceUtils.getSpanID(),
            null));
  }
}
