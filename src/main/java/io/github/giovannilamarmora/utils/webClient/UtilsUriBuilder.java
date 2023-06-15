package io.github.giovannilamarmora.utils.webClient;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import org.springframework.web.util.UriBuilder;

public class UtilsUriBuilder {
  private Function<UriBuilder, URI> uri;
  private String stringUri;

  public String getStringUri() {
    return stringUri;
  }

  public Function<UriBuilder, URI> get() {
    return uri;
  }

  public static UtilsUriBuilder toBuild() {
    return new UtilsUriBuilder();
  }

  public UtilsUriBuilder set(String path, Map<String, Object> queryParams, Object... objs) {
    StringBuilder sb = new StringBuilder();
    if (objs != null && objs.length != 0) {
      String tmp = path;
      for (Object obj : objs) {
        int endIndex = tmp.indexOf('{');
        String start = tmp.substring(0, endIndex);
        int beginIndex = tmp.indexOf('}') + 1;
        String end = tmp.substring(beginIndex);
        tmp = start + obj + end;
      }
      sb.append(tmp);
    } else sb.append(path);

    if (queryParams != null) {
      sb.append("?");
      for (Map.Entry<String, Object> e : queryParams.entrySet()) {
        sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
      }
      sb.deleteCharAt(sb.length() - 1);
    }

    uri = buildWithPath(path, queryParams, objs);
    stringUri = sb.toString();
    return this;
  }

  private Function<UriBuilder, URI> buildWithPath(
      String path, Map<String, Object> queryParams, Object... objs) {
    return utilsUriBuilder -> {
      utilsUriBuilder.path(path);
      if (queryParams != null)
        queryParams.entrySet().stream()
            .map(e -> utilsUriBuilder.queryParam(e.getKey(), e.getValue()));
      return utilsUriBuilder.build(objs);
    };
  }
}
