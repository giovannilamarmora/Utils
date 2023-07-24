package io.github.giovannilamarmora.utils.webClient;

import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.http.MediaType.TEXT_PLAIN;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class WebClientRest {
  private static final String END_STRING = "\n";
  private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
  private WebClient webClient;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());
  private String baseUrl;
  private HttpHeaders defaultHeaders;

  public void init(WebClient.Builder builder) {
    LOG.info("Initializing WebClient with:");
    if (baseUrl != null) {
      builder.baseUrl(baseUrl);
      LOG.info("BaseUrl: {}", baseUrl);
    }
    if (defaultHeaders != null) {
      LOG.info("And Default Headers: {}", defaultHeaders.entrySet().stream().toString());
    } else {
      defaultHeaders = new HttpHeaders();
      defaultHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      LOG.info(
          "And Default Headers: {}, {}",
          HttpHeaders.CONTENT_TYPE,
          MediaType.APPLICATION_JSON_VALUE);
    }
    webClient =
        builder
            .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
            .build();
  }

  public <T> Mono<ResponseEntity<T>> perform(
      HttpMethod method, UtilsUriBuilder uri, Class<T> returnType) throws JsonProcessingException {
    return perform(method, uri, null, null, returnType);
  }

  public <T> Mono<ResponseEntity<T>> perform(
      HttpMethod method, UtilsUriBuilder uri, Object body, Class<T> returnType) {
    return perform(method, uri, body, null, returnType);
  }

  public <T> Mono<ResponseEntity<T>> perform(
      HttpMethod method,
      UtilsUriBuilder url,
      Object body,
      HttpHeaders headers,
      Class<T> returnType) {
    StringBuilder logBuilder =
        new StringBuilder("Performing (")
            .append(method)
            .append(") call with WebClient to the EndPoint: ")
            .append(baseUrl != null ? baseUrl : "")
            .append(url.getStringUri())
            .append(END_STRING);

    WebClient.RequestHeadersSpec<?> buildCall = buildCall(url, headers, method, body, logBuilder);
    LOG.info(logBuilder.toString());

    switch (method) {
      case PUT:
      case PATCH:
      case POST:
        return buildCall
            .retrieve()
            .onStatus(
                HttpStatus::isError,
                clientResponse -> handleException(clientResponse, url.getStringUri()))
            .toEntity(returnType)
            .map(
                x -> {
                  try {
                    logReturnSome(
                        x.getStatusCode(),
                        x.getHeaders().toString(),
                        mapper.writeValueAsString(x.getBody()));
                    return x;
                  } catch (JsonProcessingException e) {
                    LOG.error(
                        "An error occurred during deserialize Object, message is {}",
                        e.getMessage(),
                        e);
                    throw new WebClientException(e.getMessage());
                  }
                });
      case DELETE:
      case HEAD:
      case OPTIONS:
      case GET:
        logBuilder.append("Header: ").append(headers).append(END_STRING);
        return buildCall
            .retrieve()
            .onStatus(
                HttpStatus::isError,
                clientResponse -> handleException(clientResponse, url.getStringUri()))
            .toEntity(returnType)
            .map(
                x -> {
                  try {
                    logReturnSome(
                        x.getStatusCode(),
                        x.getHeaders().toString(),
                        mapper.writeValueAsString(x.getBody()));
                    return x;
                  } catch (JsonProcessingException e) {
                    LOG.error(
                        "An error occurred during deserialize Object, message is {}",
                        e.getMessage(),
                        e);
                    throw new WebClientException(e.getMessage());
                  }
                });
      default:
        {
          return Mono.just(ResponseEntity.ok(null));
        }
    }
  }

  private WebClient.RequestHeadersSpec<?> buildCall(
      UtilsUriBuilder url,
      HttpHeaders headers,
      HttpMethod method,
      Object body,
      StringBuilder logBuilder)
      throws WebClientException {
    if (body != null) {
      try {
        logBuilder.append("Body: ").append(mapper.writeValueAsString(body)).append(END_STRING);
      } catch (JsonProcessingException e) {
        LOG.error("An error occurred during deserialize Object, message is {}", e.getMessage(), e);
        throw new WebClientException(e.getMessage());
      }
      return webClient
          .method(method)
          .uri(url.getStringUri())
          .headers(getDefaultHttpHeaders(headers, logBuilder))
          .body(BodyInserters.fromValue(body));
    } else {
      return webClient
          .method(method)
          .uri(url.getStringUri())
          .headers(getDefaultHttpHeaders(headers, logBuilder));
    }
  }

  private Consumer<HttpHeaders> getDefaultHttpHeaders(
      HttpHeaders headerList, StringBuilder logBuilder) {
    if (headerList == null) {
      logBuilder.append("Header: ").append(defaultHeaders).append(END_STRING);
      return headers -> {
        headers.putAll(defaultHeaders);
      };
    } else {
      headerList.addIfAbsent(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
      logBuilder.append("Header: ").append(headerList).append(END_STRING);
      return headers -> {
        headers.putAll(headerList);
      };
    }
  }

  private <T> void logReturnSome(HttpStatus code, String headers, String jsonBody) {
    LOG.info(
        "Status code: "
            + code
            + END_STRING
            + "Headers: "
            + headers
            + END_STRING
            + "Body: "
            + jsonBody);
  }

  private Mono<? extends Throwable> handleException(ClientResponse response, String uri) {
    return response
        .toEntity(Object.class)
        .flatMap(
            w -> {
              try {
                logReturnSome(
                    w.getStatusCode(),
                    w.getHeaders().toString(),
                    mapper.writeValueAsString(w.getBody()));
                String message =
                    "An error happened while calling the API: "
                        + baseUrl
                        + uri
                        + ", Status Code: "
                        + w.getStatusCode()
                        + " and body ";
                throw new WebClientException(message + mapper.writeValueAsString(w.getBody()));
              } catch (JsonProcessingException e) {
                throw new WebClientException(e.getMessage());
              }
            });
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public void setDefaultHeaders(HttpHeaders defaultHeaders) {
    this.defaultHeaders = defaultHeaders;
  }

  private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
    clientCodecConfigurer
        .customCodecs()
        .encoder(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_HTML));
    clientCodecConfigurer
        .customCodecs()
        .decoder(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_HTML));
    clientCodecConfigurer
        .customCodecs()
        .encoder(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_PLAIN));
    clientCodecConfigurer
        .customCodecs()
        .decoder(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_PLAIN));
  }
}
