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
public class BenchmarkTranslation3d {
  @JsonAttribute("x")
  @JsonProperty("x")
  @JSONField(name = "x")
  public double x;

  @JsonAttribute("y")
  @JsonProperty("y")
  @JSONField(name = "y")
  public double y;

  @JsonAttribute("z")
  @JsonProperty("z")
  @JSONField(name = "z")
  public double z;

  public BenchmarkTranslation3d() {
  }

  @JsonConstructor
  @JsonCreator
  @JSONCreator(parameterNames = {"x", "y", "z"})
  public BenchmarkTranslation3d(
      @JsonAttribute("x") @JsonProperty("x") double x,
      @JsonAttribute("y") @JsonProperty("y") double y,
      @JsonAttribute("z") @JsonProperty("z") double z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkTranslation3d other
        && x == other.x
        && y == other.y
        && z == other.z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y, z);
  }
}
