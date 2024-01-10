package io.github.giovannilamarmora.utils.generic;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GenericDTO {

  private Long id;

  private LocalDateTime creationDate;

  private LocalDateTime updateDate;

  private LocalDateTime deletedDate;
}
