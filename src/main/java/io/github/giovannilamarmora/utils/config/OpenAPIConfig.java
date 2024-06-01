package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.logger.LoggerFilter;
import io.github.giovannilamarmora.utils.utilities.FilesUtils;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.slf4j.Logger;
import org.springframework.util.ObjectUtils;

public class OpenAPIConfig {

  private static final Logger LOG = LoggerFilter.getLogger(OpenAPIConfig.class);

  public static PathItem addJSONExamplesOnResource(PathItem pathItem, Class<?>... optionalClass) {
    if (!ObjectUtils.isEmpty(pathItem.getGet())) {
      addOperations(pathItem.getGet(), optionalClass);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPost())) {
      addOperations(pathItem.getPost(), optionalClass);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPatch())) {
      addOperations(pathItem.getPatch(), optionalClass);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPut())) {
      addOperations(pathItem.getPut(), optionalClass);
    }
    if (!ObjectUtils.isEmpty(pathItem.getDelete())) {
      addOperations(pathItem.getDelete(), optionalClass);
    }
    return pathItem;
  }

  private static void addOperations(Operation operation, Class<?>... optionalClass) {
    if (!ObjectUtils.isEmpty(operation) && !ObjectUtils.isEmpty(operation.getResponses())) {
      operation
          .getResponses()
          .forEach(
              (statusCode, apiResponse) -> {
                if (!ObjectUtils.isEmpty(apiResponse.getContent())
                    && !ObjectUtils.isEmpty(apiResponse.getContent().values())) {
                  apiResponse
                      .getContent()
                      .values()
                      .forEach(
                          content -> {
                            try {
                              if (!content.getExampleSetFlag()) return;
                              String fileName = getExampleFileName(content.getExample().toString());
                              LOG.debug("FileName is {}", fileName);
                              String jsonContent =
                                  FilesUtils.searchFileFromResources(fileName, optionalClass);
                              if (jsonContent != null) {
                                content.setExample(jsonContent);
                              }
                            } catch (Exception e) {
                              LOG.error(
                                  "An Exception occurred during read filename for Open API", e);
                            }
                          });
                }
              });
    }
  }

  private static String getExampleFileName(String fileName) {
    return fileName.replaceFirst("@", "");
  }
}
