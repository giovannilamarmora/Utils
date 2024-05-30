package io.github.giovannilamarmora.utils.webClient;

import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.MediaType.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.giovannilamarmora.utils.web.WebManager;
import java.net.URI;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class WebClientRest {
  private static final String END_STRING = "\n";
  private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
  private WebClient webClient;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Setter private String baseUrl;

  public void init(WebClient.Builder builder) {
    LOG.info("Initializing WebClient with:");
    if (baseUrl != null) {
      builder.baseUrl(baseUrl);
      LOG.info("BaseUrl: {}", baseUrl);
    }
    HttpClient httpClient =
        HttpClient.create().httpResponseDecoder(spec -> spec.maxHeaderSize(32 * 1024));
    webClient =
        builder
            .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
            .build();
  }

  public <T> Mono<ResponseEntity<T>> perform(
      HttpMethod method, Function<UriBuilder, URI> uri, HttpHeaders headers, Class<T> returnType) {
    return perform(method, uri, null, headers, returnType);
  }

  public <T> Mono<ResponseEntity<List<T>>> performList(
      HttpMethod method, Function<UriBuilder, URI> uri, HttpHeaders headers, Class<T> returnType) {
    return performList(method, uri, null, headers, returnType);
  }

  public <T> Mono<ResponseEntity<T>> perform(
      HttpMethod method,
      Function<UriBuilder, URI> url,
      Object body,
      HttpHeaders headers,
      Class<T> returnType) {

    StringBuilder logBuilder =
        new StringBuilder("Performing (")
            .append(method)
            .append(") call with WebClient to the EndPoint: ")
            .append(WebManager.getUrlAsString(url, baseUrl))
            .append(END_STRING);

    WebClient.RequestHeadersSpec<?> buildCall = buildCall(url, headers, method, body, logBuilder);

    return buildCall
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                handleException(clientResponse, WebManager.getUrlAsString(url, baseUrl), headers))
        .toEntity(returnType)
        .doFirst(() -> LOG.info(logBuilder.toString()))
        .map(r -> mapResponseEntity(r, WebManager.getUrlAsString(url, baseUrl)));
  }

  public <T> Mono<ResponseEntity<List<T>>> performList(
      HttpMethod method,
      Function<UriBuilder, URI> url,
      Object body,
      HttpHeaders headers,
      Class<T> returnType) {
    StringBuilder logBuilder =
        new StringBuilder("Performing (")
            .append(method)
            .append(") call with WebClient to the EndPoint: ")
            .append(WebManager.getUrlAsString(url, baseUrl))
            .append(url.toString())
            .append(END_STRING);

    WebClient.RequestHeadersSpec<?> buildCall = buildCall(url, headers, method, body, logBuilder);

    return buildCall
        .retrieve()
        .onStatus(
            HttpStatusCode::isError,
            clientResponse ->
                handleException(clientResponse, WebManager.getUrlAsString(url, baseUrl), headers))
        .toEntityList(returnType)
        .doFirst(() -> LOG.info(logBuilder.toString()))
        .doOnSuccess(r -> mapResponseEntity(r, WebManager.getUrlAsString(url, baseUrl)));
  }

  private WebClient.RequestHeadersSpec<?> buildCall(
      Function<UriBuilder, URI> url,
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
        throw new WebClientException(e.getMessage(), e);
      }
      return webClient
          .method(method)
          .uri(url)
          .headers(getDefaultHttpHeaders(headers, logBuilder))
          .body(BodyInserters.fromValue(body));
    } else {
      return webClient.method(method).uri(url).headers(getDefaultHttpHeaders(headers, logBuilder));
    }
  }

  private Consumer<HttpHeaders> getDefaultHttpHeaders(
      HttpHeaders headerList, StringBuilder logBuilder) {
    logBuilder.append("Header: ").append(headerList).append(END_STRING);
    return headers -> {
      headers.putAll(headerList);
    };
  }

  private <T> void logReturnSome(HttpStatusCode code, String uri, String headers, String jsonBody) {
    LOG.info(
        "Received Response from {} with Status code: {}"
            + END_STRING
            + "Headers: {}"
            + END_STRING
            + "Body: {}",
        uri,
        code,
        headers,
        jsonBody);
  }

  private <T> ResponseEntity<T> mapResponseEntity(ResponseEntity<T> x, String uri) {
    try {
      logReturnSome(
          x.getStatusCode(),
          uri,
          x.getHeaders().toString(),
          mapper.writeValueAsString(x.getBody()));
      return x;
    } catch (JsonProcessingException e) {
      LOG.error("An error occurred during deserialize Object, message is {}", e.getMessage(), e);
      throw new WebClientException(e.getMessage(), e);
    }
  }

  private Mono<? extends Throwable> handleException(
      ClientResponse response, String uri, HttpHeaders headers) {
    List<String> getHeaders = headers.get(HttpHeaders.CONTENT_TYPE);
    if (getHeaders != null
        && !getHeaders.isEmpty()
        && (getHeaders.contains(TEXT_XML_VALUE)
            || getHeaders.contains(TEXT_PLAIN_VALUE)
            || getHeaders.contains(APPLICATION_FORM_URLENCODED_VALUE)))
      return response.toEntity(String.class).flatMap(w -> flatMapResponse(w, uri));
    else return response.toEntity(Object.class).flatMap(w -> flatMapResponse(w, uri));
  }

  private <R> Mono<R> flatMapResponse(ResponseEntity<?> w, String uri) {
    try {
      logReturnSome(
          w.getStatusCode(),
          uri,
          w.getHeaders().toString(),
          mapper.writeValueAsString(w.getBody()));
      StringBuilder message =
          new StringBuilder("An error happened while calling the API: ")
              .append(baseUrl != null ? baseUrl : "")
              .append(uri)
              .append(", Status Code: ")
              .append(w.getStatusCode())
              .append(", and body message ")
              .append(mapper.writeValueAsString(w.getBody()));
      throw new WebClientException(message.toString());
    } catch (JsonProcessingException e) {
      throw new WebClientException(e.getMessage(), e);
    }
  }

  private void acceptedCodecs(ClientCodecConfigurer clientCodecConfigurer) {
    clientCodecConfigurer
        .customCodecs()
        .register(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_HTML));
    clientCodecConfigurer
        .customCodecs()
        .register(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_HTML));
    clientCodecConfigurer
        .customCodecs()
        .register(new Jackson2JsonEncoder(new ObjectMapper(), TEXT_PLAIN));
    clientCodecConfigurer
        .customCodecs()
        .register(new Jackson2JsonDecoder(new ObjectMapper(), TEXT_PLAIN));
  }
}
