package io.swagger.pipelinemodel;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.SupportedLanguages;
import io.swagger.model.SupportedScripts;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * ConfigSchema
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-06-09T06:12:11.900779753Z[GMT]")


public class ConfigSchema   {
  @JsonProperty("modelId")
  private String modelId = null;

  @JsonProperty("serviceId")
  private String serviceId = null;

  @JsonProperty("sourceLanguage")
  private SupportedLanguages sourceLanguage = null;

  @JsonProperty("targetLanguage")
  private SupportedLanguages targetLanguage = null;

  @JsonProperty("sourceScriptCode")
  private SupportedScripts sourceScriptCode = null;

  @JsonProperty("targetScriptCode")
  private SupportedScripts targetScriptCode = null;

  public ConfigSchema modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * Unique identifier of model
   * @return modelId
   **/
  @Schema(example = "63c9586ea0e5e81614ff96a8", required = true, description = "Unique identifier of model")
      @NotNull

    public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public ConfigSchema serviceId(String serviceId) {
    this.serviceId = serviceId;
    return this;
  }

  /**
   * specific id for the service
   * @return serviceId
   **/
  @Schema(example = "ai4bharat/speech-to-speech-gpu--t4", required = true, description = "specific id for the service")
      @NotNull

    public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public ConfigSchema sourceLanguage(SupportedLanguages sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
    return this;
  }

  /**
   * Get sourceLanguage
   * @return sourceLanguage
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public SupportedLanguages getSourceLanguage() {
    return sourceLanguage;
  }

  public void setSourceLanguage(SupportedLanguages sourceLanguage) {
    this.sourceLanguage = sourceLanguage;
  }

  public ConfigSchema targetLanguage(SupportedLanguages targetLanguage) {
    this.targetLanguage = targetLanguage;
    return this;
  }

  /**
   * Get targetLanguage
   * @return targetLanguage
   **/
  @Schema(description = "")
  
    @Valid
    public SupportedLanguages getTargetLanguage() {
    return targetLanguage;
  }

  public void setTargetLanguage(SupportedLanguages targetLanguage) {
    this.targetLanguage = targetLanguage;
  }

  public ConfigSchema sourceScriptCode(SupportedScripts sourceScriptCode) {
    this.sourceScriptCode = sourceScriptCode;
    return this;
  }

  /**
   * Get sourceScriptCode
   * @return sourceScriptCode
   **/
  @Schema(description = "")
  
    @Valid
    public SupportedScripts getSourceScriptCode() {
    return sourceScriptCode;
  }

  public void setSourceScriptCode(SupportedScripts sourceScriptCode) {
    this.sourceScriptCode = sourceScriptCode;
  }

  public ConfigSchema targetScriptCode(SupportedScripts targetScriptCode) {
    this.targetScriptCode = targetScriptCode;
    return this;
  }

  /**
   * Get targetScriptCode
   * @return targetScriptCode
   **/
  @Schema(description = "")
  
    @Valid
    public SupportedScripts getTargetScriptCode() {
    return targetScriptCode;
  }

  public void setTargetScriptCode(SupportedScripts targetScriptCode) {
    this.targetScriptCode = targetScriptCode;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ConfigSchema configSchema = (ConfigSchema) o;
    return Objects.equals(this.modelId, configSchema.modelId) &&
        Objects.equals(this.serviceId, configSchema.serviceId) &&
        Objects.equals(this.sourceLanguage, configSchema.sourceLanguage) &&
        Objects.equals(this.targetLanguage, configSchema.targetLanguage) &&
        Objects.equals(this.sourceScriptCode, configSchema.sourceScriptCode) &&
        Objects.equals(this.targetScriptCode, configSchema.targetScriptCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelId, serviceId, sourceLanguage, targetLanguage, sourceScriptCode, targetScriptCode);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConfigSchema {\n");
    
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    serviceId: ").append(toIndentedString(serviceId)).append("\n");
    sb.append("    sourceLanguage: ").append(toIndentedString(sourceLanguage)).append("\n");
    sb.append("    targetLanguage: ").append(toIndentedString(targetLanguage)).append("\n");
    sb.append("    sourceScriptCode: ").append(toIndentedString(sourceScriptCode)).append("\n");
    sb.append("    targetScriptCode: ").append(toIndentedString(targetScriptCode)).append("\n");
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
