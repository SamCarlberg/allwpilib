package wpilib.robot;

import com.alibaba.fastjson2.annotation.JSONCreator;
import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.avaje.jsonb.Json;
import java.util.Objects;
import org.wpilib.util.json.JsonAttribute;
import org.wpilib.util.json.JsonConstructor;

@Json
public class BenchmarkPose3d {
  @JsonAttribute("translation")
  @JsonProperty("translation")
  @JSONField(name = "translation")
  public BenchmarkTranslation3d translation;

  @JsonAttribute("rotation")
  @JsonProperty("rotation")
  @JSONField(name = "rotation")
  public BenchmarkRotation3d rotation;

  public BenchmarkPose3d() {}

  @JsonCreator
  @JsonConstructor
  @JSONCreator(parameterNames = { "translation", "rotation" })
  public BenchmarkPose3d(
      @JsonAttribute("translation") @JsonProperty("translation") BenchmarkTranslation3d translation,
      @JsonAttribute("rotation") @JsonProperty("rotation") BenchmarkRotation3d rotation) {
    this.translation = translation;
    this.rotation = rotation;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkPose3d other
        && Objects.equals(translation, other.translation)
        && Objects.equals(rotation, other.rotation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(translation, rotation);
  }
}
