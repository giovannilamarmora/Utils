package io.github.giovannilamarmora.utils.webClient;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import lombok.Getter;
import org.springframework.web.util.UriBuilder;

public class UtilsUriBuilder {
  private Function<UriBuilder, URI> uri;
  @Getter private String stringUri;

  public Function<UriBuilder, URI> get() {
    return uri;
  }

  public static UtilsUriBuilder toBuild() {
    return new UtilsUriBuilder();
  }

  public UtilsUriBuilder set(String path, Map<String, Object> queryParams) {
    StringBuilder pathBuilder = new StringBuilder(path);

    if (queryParams != null) {
      for (String key : queryParams.keySet()) {
        if (pathBuilder.toString().contains("{" + key + "}")) {
          pathBuilder =
              new StringBuilder(
                  pathBuilder.toString().replace("{" + key + "}", queryParams.get(key).toString()));
        } else if (pathBuilder.toString().contains(":" + key)) {
          pathBuilder =
              new StringBuilder(
                  pathBuilder.toString().replace(":" + key, queryParams.get(key).toString()));
        } else {
          if (!pathBuilder.toString().contains("?")) {
            pathBuilder.append("?");
          } else {
            pathBuilder.append("&");
          }
          pathBuilder.append(key).append("=").append(queryParams.get(key));
        }
      }
    }

    stringUri = pathBuilder.toString();

    return this;
  }

  public static Function<UriBuilder, URI> buildUri(String url, Map<String, Object> param) {
    return uriBuilder -> {
      UriBuilder builder = uriBuilder.path(url);
      if (param != null && !param.isEmpty()) {
        for (Map.Entry<String, Object> entry : param.entrySet()) {
          builder.queryParam(entry.getKey(), entry.getValue());
        }
      }
      return builder.build();
    };
  }
}
