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
public class BenchmarkRotation3d {
  @JsonAttribute("quaternion")
  @JsonProperty("quaternion")
  @JSONField(name = "quaternion")
  public BenchmarkQuaternion quaternion;

  public BenchmarkRotation3d() {}

  @JsonCreator
  @JSONCreator(parameterNames = { "quaternion" })
  @JsonConstructor
  public BenchmarkRotation3d(
      @JsonAttribute("quaternion") @JsonProperty("quaternion") BenchmarkQuaternion q) {
    this.quaternion = q;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkRotation3d other && Objects.equals(quaternion, other.quaternion);
  }

  @Override
  public int hashCode() {
    return Objects.hash(quaternion);
  }
}
