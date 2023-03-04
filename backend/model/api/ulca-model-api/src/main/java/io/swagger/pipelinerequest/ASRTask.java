package io.swagger.pipelinerequest;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import io.swagger.model.SupportedTasks;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ASRTask
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-04T10:09:49.734767395Z[GMT]")


public class ASRTask  implements PipelineTask {
  @JsonProperty("taskType")
  private SupportedTasks taskType = null;

  @JsonProperty("config")
  private ASRRequestConfig config = null;

  public ASRTask taskType(SupportedTasks taskType) {
    this.taskType = taskType;
    return this;
  }

  /**
   * Get taskType
   * @return taskType
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public SupportedTasks getTaskType() {
    return taskType;
  }

  public void setTaskType(SupportedTasks taskType) {
    this.taskType = taskType;
  }

  public ASRTask config(ASRRequestConfig config) {
    this.config = config;
    return this;
  }

  /**
   * Get config
   * @return config
   **/
  @Schema(description = "")
  
    @Valid
    public ASRRequestConfig getConfig() {
    return config;
  }

  public void setConfig(ASRRequestConfig config) {
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
    ASRTask asRTask = (ASRTask) o;
    return Objects.equals(this.taskType, asRTask.taskType) &&
        Objects.equals(this.config, asRTask.config);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskType, config);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ASRTask {\n");
    
    sb.append("    taskType: ").append(toIndentedString(taskType)).append("\n");
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
