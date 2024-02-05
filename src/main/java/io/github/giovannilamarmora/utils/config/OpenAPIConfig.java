package io.github.giovannilamarmora.utils.config;

import io.github.giovannilamarmora.utils.utilities.FilesUtils;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;

public class OpenAPIConfig {

  private static final Logger LOG = LoggerFactory.getLogger(OpenAPIConfig.class);

  public static PathItem addExamples(PathItem pathItem, ResourceLoader resourceLoader) {
    if (!ObjectUtils.isEmpty(pathItem.getGet())) {
      addOperations(pathItem.getGet(), resourceLoader);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPost())) {
      addOperations(pathItem.getPost(), resourceLoader);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPatch())) {
      addOperations(pathItem.getPatch(), resourceLoader);
    }
    if (!ObjectUtils.isEmpty(pathItem.getPut())) {
      addOperations(pathItem.getPut(), resourceLoader);
    }
    if (!ObjectUtils.isEmpty(pathItem.getDelete())) {
      addOperations(pathItem.getDelete(), resourceLoader);
    }
    return pathItem;
  }

  private static void addOperations(Operation operation, ResourceLoader resourceLoader) {
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
                                  FilesUtils.searchFileFromResources(fileName, resourceLoader);
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
