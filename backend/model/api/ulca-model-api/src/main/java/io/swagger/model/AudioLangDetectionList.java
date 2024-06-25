package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.model.AudioFile;
import io.swagger.model.LangDetectionPrediction;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * AudioLangDetectionList
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2024-06-25T06:10:19.027377971Z[GMT]")


public class AudioLangDetectionList   {
  @JsonProperty("audio")
  private AudioFile audio = null;

  @JsonProperty("langPrediction")
  @Valid
  private List<LangDetectionPrediction> langPrediction = new ArrayList<LangDetectionPrediction>();

  public AudioLangDetectionList audio(AudioFile audio) {
    this.audio = audio;
    return this;
  }

  /**
   * Get audio
   * @return audio
   **/
  @Schema(required = true, description = "")
      @NotNull

    @Valid
    public AudioFile getAudio() {
    return audio;
  }

  public void setAudio(AudioFile audio) {
    this.audio = audio;
  }

  public AudioLangDetectionList langPrediction(List<LangDetectionPrediction> langPrediction) {
    this.langPrediction = langPrediction;
    return this;
  }

  public AudioLangDetectionList addLangPredictionItem(LangDetectionPrediction langPredictionItem) {
    this.langPrediction.add(langPredictionItem);
    return this;
  }

  /**
   * list of
   * @return langPrediction
   **/
  @Schema(required = true, description = "list of")
      @NotNull
    @Valid
    public List<LangDetectionPrediction> getLangPrediction() {
    return langPrediction;
  }

  public void setLangPrediction(List<LangDetectionPrediction> langPrediction) {
    this.langPrediction = langPrediction;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AudioLangDetectionList audioLangDetectionList = (AudioLangDetectionList) o;
    return Objects.equals(this.audio, audioLangDetectionList.audio) &&
        Objects.equals(this.langPrediction, audioLangDetectionList.langPrediction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(audio, langPrediction);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AudioLangDetectionList {\n");
    
    sb.append("    audio: ").append(toIndentedString(audio)).append("\n");
    sb.append("    langPrediction: ").append(toIndentedString(langPrediction)).append("\n");
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
