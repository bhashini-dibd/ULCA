package io.swagger.pipelinerequest;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * TranslationTask
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-03T08:56:06.445513096Z[GMT]")


public class TranslationTask  implements PipelineTask {
  @JsonProperty("type")
  private SupportedTasks type = null;

  @JsonProperty("config")
  private TranslationRequestConfig config = null;

  public TranslationTask type(SupportedTasks type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public SupportedTasks getType() {
    return type;
  }

  public void setType(SupportedTasks type) {
    this.type = type;
  }

  public TranslationTask config(TranslationRequestConfig config) {
    this.config = config;
    return this;
  }

  /**
   * Get config
   * @return config
   **/
  @Schema(description = "")
  
    @Valid
    public TranslationRequestConfig getConfig() {
    return config;
  }

  public void setConfig(TranslationRequestConfig config) {
    this.config = config;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TranslationTask translationTask = (TranslationTask) o;
    return Objects.equals(this.type, translationTask.type) &&
        Objects.equals(this.config, translationTask.config);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, config);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TranslationTask {\n");
    
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    config: ").append(toIndentedString(config)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
