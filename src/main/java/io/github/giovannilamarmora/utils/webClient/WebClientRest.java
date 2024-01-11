package io.github.giovannilamarmora.utils.webClient;

import static org.springframework.http.MediaType.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.giovannilamarmora.utils.config.UtilsPropertiesManager;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

@Component
public class WebClientRest {
  private static final String END_STRING = "\n";
  private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
  private WebClient webClient;
  private final Logger LOG = LoggerFactory.getLogger(this.getClass());

  @Setter private String baseUrl;
  private HttpHeaders defaultHeaders;
  @Autowired private UtilsPropertiesManager propertyManager;

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
    HttpClient httpClient =
        HttpClient.create()
            // .doOnConnected(
            //    connection ->
            //        connection
            //            .addHandlerFirst(
            //                new ReadTimeoutHandler(
            //                    propertyManager.getReadTimeout(), TimeUnit.MILLISECONDS))
            //            .addHandlerFirst(
            //                new WriteTimeoutHandler(
            //                    propertyManager.getWriteTimeout(), TimeUnit.MILLISECONDS)))
            .httpResponseDecoder(spec -> spec.maxHeaderSize(32 * 1024));
    // .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, propertyManager.getConnectionTimeout());

    webClient =
        builder
            .exchangeStrategies(ExchangeStrategies.builder().codecs(this::acceptedCodecs).build())
            .clientConnector(new ReactorClientHttpConnector(httpClient))
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
      case DELETE:
      case HEAD:
      case OPTIONS:
      case GET:
        return buildCall
            .retrieve()
            .onStatus(
                HttpStatus::isError,
                clientResponse -> handleException(clientResponse, url.getStringUri(), headers))
            .toEntity(returnType)
            .map(x -> mapResponseEntity(x, returnType, headers));
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
        throw new WebClientException(e.getMessage(), e);
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

  private <T> ResponseEntity<T> mapResponseEntity(
      ResponseEntity<T> x, Class<T> returnType, HttpHeaders headers) {
    try {
      logReturnSome(
          x.getStatusCode(), x.getHeaders().toString(), mapper.writeValueAsString(x.getBody()));
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

  private <R> Mono<R> flatMapResponse(ResponseEntity w, String uri) {
    try {
      logReturnSome(
          w.getStatusCode(), w.getHeaders().toString(), mapper.writeValueAsString(w.getBody()));
      StringBuilder message =
          new StringBuilder("An error happened while calling the API: ")
              .append(baseUrl != null ? baseUrl : "")
              .append(uri)
              .append(", Status Code: ")
              .append(w.getStatusCode())
              .append(", and body message ")
              .append(w.getBody());
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
