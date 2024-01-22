package io.swagger.pipelinerequest;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.model.AudioBitsPerSample;
import io.swagger.model.AudioChannel;
import io.swagger.model.AudioFormat;
import io.swagger.model.AudioPostProcessors;
import io.swagger.model.Domain;
import io.swagger.model.Encoding;
import io.swagger.model.LanguagePair;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AudioConfig
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2023-03-02T08:00:21.046011704Z[GMT]")


public class AudioConfig   {
  @JsonProperty("modelId")
  private String modelId = null;

  @JsonProperty("language")
  private LanguagePair language = null;

  @JsonProperty("audioFormat")
  private AudioFormat audioFormat = null;

  @JsonProperty("channel")
  private AudioChannel channel = null;

  @JsonProperty("samplingRate")
  private BigDecimal samplingRate = null;

  @JsonProperty("bitsPerSample")
  private AudioBitsPerSample bitsPerSample = null;

  @JsonProperty("transcriptionFormat")
  private AudioConfigTranscriptionFormat transcriptionFormat = null;

  @JsonProperty("postProcessors")
  private AudioPostProcessors postProcessors = null;

  @JsonProperty("domain")
  private Domain domain = null;

  @JsonProperty("detailed")
  private Boolean detailed = null;

  @JsonProperty("punctuation")
  private Boolean punctuation = null;

  /**
   * Gets or Sets model
   */
  public enum ModelEnum {
    COMMAND_AND_SEARCH("command_and_search"),
    
    PHONE_CALL("phone_call"),
    
    VIDEO("video"),
    
    DEFAULT("default");

    private String value;

    ModelEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static ModelEnum fromValue(String text) {
      for (ModelEnum b : ModelEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("model")
  private ModelEnum model = null;

  @JsonProperty("encoding")
  private Encoding encoding = null;

  public AudioConfig modelId(String modelId) {
    this.modelId = modelId;
    return this;
  }

  /**
   * Unique identifier of model
   * @return modelId
   **/
  @Schema(example = "103", description = "Unique identifier of model")
  
    public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public AudioConfig language(LanguagePair language) {
    this.language = language;
    return this;
  }

  /**
   * Get language
   * @return language
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public LanguagePair getLanguage() {
    return language;
  }

  public void setLanguage(LanguagePair language) {
    this.language = language;
  }

  public AudioConfig audioFormat(AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
    return this;
  }

  /**
   * Get audioFormat
   * @return audioFormat
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public AudioFormat getAudioFormat() {
    return audioFormat;
  }

  public void setAudioFormat(AudioFormat audioFormat) {
    this.audioFormat = audioFormat;
  }

  public AudioConfig channel(AudioChannel channel) {
    this.channel = channel;
    return this;
  }

  /**
   * Get channel
   * @return channel
   **/
  @Schema(description = "")
  
    @Valid
    public AudioChannel getChannel() {
    return channel;
  }

  public void setChannel(AudioChannel channel) {
    this.channel = channel;
  }

  public AudioConfig samplingRate(BigDecimal samplingRate) {
    this.samplingRate = samplingRate;
    return this;
  }

  /**
   * sample rate of the audio file in kHz
   * @return samplingRate
   **/
  @Schema(example = "44", description = "sample rate of the audio file in kHz")
  
    @Valid
    public BigDecimal getSamplingRate() {
    return samplingRate;
  }

  public void setSamplingRate(BigDecimal samplingRate) {
    this.samplingRate = samplingRate;
  }

  public AudioConfig bitsPerSample(AudioBitsPerSample bitsPerSample) {
    this.bitsPerSample = bitsPerSample;
    return this;
  }

  /**
   * Get bitsPerSample
   * @return bitsPerSample
   **/
  @Schema(description = "")
  
    @Valid
    public AudioBitsPerSample getBitsPerSample() {
    return bitsPerSample;
  }

  public void setBitsPerSample(AudioBitsPerSample bitsPerSample) {
    this.bitsPerSample = bitsPerSample;
  }

  public AudioConfig transcriptionFormat(AudioConfigTranscriptionFormat transcriptionFormat) {
    this.transcriptionFormat = transcriptionFormat;
    return this;
  }

  /**
   * Get transcriptionFormat
   * @return transcriptionFormat
   **/
  @Schema(description = "")
  
    @Valid
    public AudioConfigTranscriptionFormat getTranscriptionFormat() {
    return transcriptionFormat;
  }

  public void setTranscriptionFormat(AudioConfigTranscriptionFormat transcriptionFormat) {
    this.transcriptionFormat = transcriptionFormat;
  }

  public AudioConfig postProcessors(AudioPostProcessors postProcessors) {
    this.postProcessors = postProcessors;
    return this;
  }

  /**
   * Get postProcessors
   * @return postProcessors
   **/
  @Schema(description = "")
  
    @Valid
    public AudioPostProcessors getPostProcessors() {
    return postProcessors;
  }

  public void setPostProcessors(AudioPostProcessors postProcessors) {
    this.postProcessors = postProcessors;
  }

  public AudioConfig domain(Domain domain) {
    this.domain = domain;
    return this;
  }

  /**
   * Get domain
   * @return domain
   **/
  @Schema(description = "")
  
    @Valid
    public Domain getDomain() {
    return domain;
  }

  public void setDomain(Domain domain) {
    this.domain = domain;
  }

  public AudioConfig detailed(Boolean detailed) {
    this.detailed = detailed;
    return this;
  }

  /**
   * to specify whether details are required in output like SNR, sampling rate
   * @return detailed
   **/
  @Schema(description = "to specify whether details are required in output like SNR, sampling rate")
  
    public Boolean isDetailed() {
    return detailed;
  }

  public void setDetailed(Boolean detailed) {
    this.detailed = detailed;
  }

  public AudioConfig punctuation(Boolean punctuation) {
    this.punctuation = punctuation;
    return this;
  }

  /**
   * Get punctuation
   * @return punctuation
   **/
  @Schema(example = "true", description = "")
  
    public Boolean isPunctuation() {
    return punctuation;
  }

  public void setPunctuation(Boolean punctuation) {
    this.punctuation = punctuation;
  }

  public AudioConfig model(ModelEnum model) {
    this.model = model;
    return this;
  }

  /**
   * Get model
   * @return model
   **/
  @Schema(description = "")
  
    public ModelEnum getModel() {
    return model;
  }

  public void setModel(ModelEnum model) {
    this.model = model;
  }

  public AudioConfig encoding(Encoding encoding) {
    this.encoding = encoding;
    return this;
  }

  /**
   * Get encoding
   * @return encoding
   **/
  @Schema(description = "")
  
    @Valid
    public Encoding getEncoding() {
    return encoding;
  }

  public void setEncoding(Encoding encoding) {
    this.encoding = encoding;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AudioConfig audioConfig = (AudioConfig) o;
    return Objects.equals(this.modelId, audioConfig.modelId) &&
        Objects.equals(this.language, audioConfig.language) &&
        Objects.equals(this.audioFormat, audioConfig.audioFormat) &&
        Objects.equals(this.channel, audioConfig.channel) &&
        Objects.equals(this.samplingRate, audioConfig.samplingRate) &&
        Objects.equals(this.bitsPerSample, audioConfig.bitsPerSample) &&
        Objects.equals(this.transcriptionFormat, audioConfig.transcriptionFormat) &&
        Objects.equals(this.postProcessors, audioConfig.postProcessors) &&
        Objects.equals(this.domain, audioConfig.domain) &&
        Objects.equals(this.detailed, audioConfig.detailed) &&
        Objects.equals(this.punctuation, audioConfig.punctuation) &&
        Objects.equals(this.model, audioConfig.model) &&
        Objects.equals(this.encoding, audioConfig.encoding);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modelId, language, audioFormat, channel, samplingRate, bitsPerSample, transcriptionFormat, postProcessors, domain, detailed, punctuation, model, encoding);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AudioConfig {\n");
    
    sb.append("    modelId: ").append(toIndentedString(modelId)).append("\n");
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    audioFormat: ").append(toIndentedString(audioFormat)).append("\n");
    sb.append("    channel: ").append(toIndentedString(channel)).append("\n");
    sb.append("    samplingRate: ").append(toIndentedString(samplingRate)).append("\n");
    sb.append("    bitsPerSample: ").append(toIndentedString(bitsPerSample)).append("\n");
    sb.append("    transcriptionFormat: ").append(toIndentedString(transcriptionFormat)).append("\n");
    sb.append("    postProcessors: ").append(toIndentedString(postProcessors)).append("\n");
    sb.append("    domain: ").append(toIndentedString(domain)).append("\n");
    sb.append("    detailed: ").append(toIndentedString(detailed)).append("\n");
    sb.append("    punctuation: ").append(toIndentedString(punctuation)).append("\n");
    sb.append("    model: ").append(toIndentedString(model)).append("\n");
    sb.append("    encoding: ").append(toIndentedString(encoding)).append("\n");
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
