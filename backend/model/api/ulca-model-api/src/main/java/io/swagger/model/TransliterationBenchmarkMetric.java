package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * transliteration benchmark score
 */
@Schema(description = "transliteration benchmark score")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-12-22T12:57:01.789Z[GMT]")


public class TransliterationBenchmarkMetric   {
  /**
   * the automatic evaluation metric name
   */
  public enum MetricNameEnum {
    CER("cer"),
    
    TOP_1_ACCURACY("top-1-accuracy");

    private String value;

    MetricNameEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static MetricNameEnum fromValue(String text) {
      for (MetricNameEnum b : MetricNameEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }
  @JsonProperty("metricName")
  private MetricNameEnum metricName = null;

  @JsonProperty("score")
  private BigDecimal score = null;

  public TransliterationBenchmarkMetric metricName(MetricNameEnum metricName) {
    this.metricName = metricName;
    return this;
  }

  /**
   * the automatic evaluation metric name
   * @return metricName
   **/
  @Schema(required = true, description = "the automatic evaluation metric name")
      @NotNull

    public MetricNameEnum getMetricName() {
    return metricName;
  }

  public void setMetricName(MetricNameEnum metricName) {
    this.metricName = metricName;
  }

  public TransliterationBenchmarkMetric score(BigDecimal score) {
    this.score = score;
    return this;
  }

  /**
   * is the score as per defined metric for this benchmark.
   * @return score
   **/
  @Schema(required = true, description = "is the score as per defined metric for this benchmark.")
      @NotNull

    @Valid
    public BigDecimal getScore() {
    return score;
  }

  public void setScore(BigDecimal score) {
    this.score = score;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TransliterationBenchmarkMetric transliterationBenchmarkMetric = (TransliterationBenchmarkMetric) o;
    return Objects.equals(this.metricName, transliterationBenchmarkMetric.metricName) &&
        Objects.equals(this.score, transliterationBenchmarkMetric.score);
  }

  @Override
  public int hashCode() {
    return Objects.hash(metricName, score);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class TransliterationBenchmarkMetric {\n");
    
    sb.append("    metricName: ").append(toIndentedString(metricName)).append("\n");
    sb.append("    score: ").append(toIndentedString(score)).append("\n");
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
