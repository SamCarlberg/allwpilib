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
public class BenchmarkAprilTag {
  @JsonAttribute("ID")
  @JsonProperty("ID")
  @JSONField(name = "ID")
  public int ID;

  @JsonAttribute("pose")
  @JsonProperty("pose")
  @JSONField(name = "pose")
  public BenchmarkPose3d pose;

  public BenchmarkAprilTag() {}

  @JsonConstructor
  @JsonCreator
  @JSONCreator(parameterNames = {"ID", "pose"})
  public BenchmarkAprilTag(
      @JsonAttribute("ID") @JsonProperty("ID") int ID,
      @JsonAttribute("pose") @JsonProperty("pose") BenchmarkPose3d pose) {
    this.ID = ID;
    this.pose = pose;
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof BenchmarkAprilTag other && ID == other.ID && Objects.equals(pose, other.pose);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ID, pose);
  }
}
