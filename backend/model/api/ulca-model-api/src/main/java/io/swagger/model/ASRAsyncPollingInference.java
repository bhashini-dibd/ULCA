package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.ASRResponse;
import io.swagger.model.ModelProcessingType;
import io.swagger.model.PollingRequest;
import io.swagger.model.SupportedTasks;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ASRAsyncPollingInference
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-02T03:55:25.562740452Z[GMT]")


public class ASRAsyncPollingInference  implements OneOfAsyncApiDetailsAsyncApiPollingSchema {
  @JsonProperty("taskType")
  private SupportedTasks taskType = null;

  @JsonProperty("modelProcessingType")
  private ModelProcessingType modelProcessingType = null;

  @JsonProperty("request")
  private PollingRequest request = null;

  @JsonProperty("response")
  private ASRResponse response = null;

  public ASRAsyncPollingInference taskType(SupportedTasks taskType) {
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

  public ASRAsyncPollingInference modelProcessingType(ModelProcessingType modelProcessingType) {
    this.modelProcessingType = modelProcessingType;
    return this;
  }

  /**
   * Get modelProcessingType
   * @return modelProcessingType
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public ModelProcessingType getModelProcessingType() {
    return modelProcessingType;
  }

  public void setModelProcessingType(ModelProcessingType modelProcessingType) {
    this.modelProcessingType = modelProcessingType;
  }

  public ASRAsyncPollingInference request(PollingRequest request) {
    this.request = request;
    return this;
  }

  /**
   * Get request
   * @return request
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public PollingRequest getRequest() {
    return request;
  }

  public void setRequest(PollingRequest request) {
    this.request = request;
  }

  public ASRAsyncPollingInference response(ASRResponse response) {
    this.response = response;
    return this;
  }

  /**
   * Get response
   * @return response
   **/
  @Schema(description = "")
  
    @Valid
    public ASRResponse getResponse() {
    return response;
  }

  public void setResponse(ASRResponse response) {
    this.response = response;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ASRAsyncPollingInference asRAsyncPollingInference = (ASRAsyncPollingInference) o;
    return Objects.equals(this.taskType, asRAsyncPollingInference.taskType) &&
        Objects.equals(this.modelProcessingType, asRAsyncPollingInference.modelProcessingType) &&
        Objects.equals(this.request, asRAsyncPollingInference.request) &&
        Objects.equals(this.response, asRAsyncPollingInference.response);
  }

  @Override
  public int hashCode() {
    return Objects.hash(taskType, modelProcessingType, request, response);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ASRAsyncPollingInference {\n");
    
    sb.append("    taskType: ").append(toIndentedString(taskType)).append("\n");
    sb.append("    modelProcessingType: ").append(toIndentedString(modelProcessingType)).append("\n");
    sb.append("    request: ").append(toIndentedString(request)).append("\n");
    sb.append("    response: ").append(toIndentedString(response)).append("\n");
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
