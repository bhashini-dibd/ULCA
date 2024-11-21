package io.swagger.pipelinerequest;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.model.LanguagePair;


/**
 * NERResponseConfig
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-11-19T09:12:35.226263225Z[GMT]")


public class NERResponseConfig   {
  @JsonProperty("serviceId")

  private String serviceId = null;

  @JsonProperty("modelId")

  private String modelId = null;

  @JsonProperty("language")

  private LanguagePair language = null;

  @JsonProperty("inputFormat")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private SupportedFormats inputFormat = null;

  @JsonProperty("outputFormat")

  @JsonInclude(JsonInclude.Include.NON_ABSENT)  // Exclude from JSON if absent
  @JsonSetter(nulls = Nulls.FAIL)    // FAIL setting if the value is null
  private SupportedFormats outputFormat = null;


  public NERResponseConfig serviceId(String serviceId) { 

    this.serviceId = serviceId;
    return this;
  }

  /**
   * Unique identifier of model
   * @return serviceId
   **/
  
  @Schema(example = "103", required = true, description = "Unique identifier of model")
  
  @NotNull
  public String getServiceId() {  
    return serviceId;
  }



  public void setServiceId(String serviceId) { 

    this.serviceId = serviceId;
  }

  public NERResponseConfig modelId(String modelId) { 

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

  public NERResponseConfig language(LanguagePair language) { 

    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   **/
  
  @Schema(required = true, description = "")
  
@Valid
  @NotNull
  public LanguagePair getLanguage() {  
    return language;
  }



  public void setLanguage(LanguagePair language) { 

    this.language = language;
  }

  public NERResponseConfig inputFormat(SupportedFormats inputFormat) { 

    this.inputFormat = inputFormat;
    return this;
  }

  /**
   * Get inputFormat
   * @return inputFormat
   **/
  
  @Schema(description = "")
  
@Valid
  public SupportedFormats getInputFormat() {  
    return inputFormat;
  }



  public void setInputFormat(SupportedFormats inputFormat) { 
    this.inputFormat = inputFormat;
  }

  public NERResponseConfig outputFormat(SupportedFormats outputFormat) { 

    this.outputFormat = outputFormat;
    return this;
  }

  /**
   * Get outputFormat
   * @return outputFormat
   **/
  
  @Schema(description = "")
  
@Valid
  public SupportedFormats getOutputFormat() {  
    return outputFormat;
  }



  public void setOutputFormat(SupportedFormats outputFormat) { 
    this.outputFormat = outputFormat;
  }

  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NERResponseConfig neRResponseConfig = (NERResponseConfig) o;
    return Objects.equals(this.serviceId, neRResponseConfig.serviceId) &&
        Objects.equals(this.modelId, neRResponseConfig.modelId) &&
        Objects.equals(this.language, neRResponseConfig.language) &&
        Objects.equals(this.inputFormat, neRResponseConfig.inputFormat) &&
        Objects.equals(this.outputFormat, neRResponseConfig.outputFormat);
  }

  @Override
  public int hashCode() {
    return Objects.hash(serviceId, modelId, language, inputFormat, outputFormat);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NERResponseConfig {\n");
    
    sb.append("    serviceId: ").append(toIndentedString(serviceId)).append("\n");
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    inputFormat: ").append(toIndentedString(inputFormat)).append("\n");
    sb.append("    outputFormat: ").append(toIndentedString(outputFormat)).append("\n");
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
