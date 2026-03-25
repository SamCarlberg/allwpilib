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
public class BenchmarkQuaternion {
  @JsonAttribute("W")
  @JsonProperty("W")
  @JSONField(name = "W")
  public double W;

  @JsonAttribute("X")
  @JsonProperty("X")
  @JSONField(name = "X")
  public double X;

  @JsonAttribute("Y")
  @JsonProperty("Y")
  @JSONField(name = "Y")
  public double Y;

  @JsonAttribute("Z")
  @JsonProperty("Z")
  @JSONField(name = "Z")
  public double Z;

  public BenchmarkQuaternion() {}

  @JsonConstructor
  @JSONCreator(parameterNames = { "W", "X", "Y", "Z"})
  @JsonCreator
  public BenchmarkQuaternion(
      @JsonAttribute("W") @JsonProperty("W") double w,
      @JsonAttribute("X") @JsonProperty("X") double x,
      @JsonAttribute("Y") @JsonProperty("Y") double y,
      @JsonAttribute("Z") @JsonProperty("Z") double z) {
    this.W = w;
    this.X = x;
    this.Y = y;
    this.Z = z;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkQuaternion other
        && W == other.W
        && X == other.X
        && Y == other.Y
        && Z == other.Z;
  }

  @Override
  public int hashCode() {
    return Objects.hash(W, X, Y, Z);
  }
}
