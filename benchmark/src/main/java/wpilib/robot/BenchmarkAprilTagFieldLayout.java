package wpilib.robot;

import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.avaje.jsonb.Json;
import java.util.List;
import java.util.Objects;
import org.wpilib.util.json.JsonAttribute;
import org.wpilib.util.json.JsonConstructor;

@Json
public class BenchmarkAprilTagFieldLayout {
  @JsonAttribute("tags")
  @JsonProperty("tags")
  @JSONField(name = "tags")
  public List<BenchmarkAprilTag> tags;

  @JsonAttribute("field")
  @JsonProperty("field")
  @JSONField(name = "field")
  public FieldDimensions field;

  public BenchmarkAprilTagFieldLayout() {}

  @JsonConstructor
  public BenchmarkAprilTagFieldLayout(
      @JsonAttribute("tags") List<BenchmarkAprilTag> tags,
      @JsonAttribute("field") FieldDimensions field) {
    this.tags = tags;
    this.field = field;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkAprilTagFieldLayout other
        && Objects.equals(tags, other.tags)
        && Objects.equals(field, other.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(tags, field);
  }

  @Json
  public static class FieldDimensions {
    @JsonAttribute("fieldLength")
    @JsonProperty("fieldLength")
    @JSONField(name = "fieldLength")
    public double fieldLength;

    @JsonAttribute("fieldWidth")
    @JsonProperty("fieldWidth")
    @JSONField(name = "fieldWidth")
    public double fieldWidth;

    public FieldDimensions() {}

    @JsonConstructor
    @JsonCreator
    @JSONCreator(parameterNames = {"fieldLength", "fieldWidth"})
    public FieldDimensions(
        @JsonAttribute("fieldLength") @JsonProperty("fieldLength") double fieldLength,
        @JsonAttribute("fieldWidth") @JsonProperty("fieldWidth") double fieldWidth) {
      this.fieldLength = fieldLength;
      this.fieldWidth = fieldWidth;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof FieldDimensions other
          && fieldLength == other.fieldLength
          && fieldWidth == other.fieldWidth;
    }

    @Override
    public int hashCode() {
      return Objects.hash(fieldLength, fieldWidth);
    }
  }
}
