package wpilib.robot;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.avaje.jsonb.Jsonb;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.wpilib.util.json.JsonDeserializer;
import org.wpilib.util.json.JsonSerializer;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Measurement(iterations = 3, time = 1)
@Fork(1)
public class JsonBenchmark {

  private BenchmarkAprilTagFieldLayout smallLayout;
  private BenchmarkAprilTagFieldLayout largeLayout;
  private String smallJson;
  private String largeJson;

  private ObjectMapper jacksonMapper;
  private Jsonb avajeJsonb;

  @Param({"small", "large"})
  public String size;

  @Setup(Level.Trial)
  public void setup() {
    // Small layout (1 tag)
    List<BenchmarkAprilTag> smallTags =
        List.of(
            new BenchmarkAprilTag(
                1,
                new BenchmarkPose3d(
                    new BenchmarkTranslation3d(1, 2, 3),
                    new BenchmarkRotation3d(new BenchmarkQuaternion(1, 0, 0, 0)))));
    smallLayout =
        new BenchmarkAprilTagFieldLayout(
            smallTags, new BenchmarkAprilTagFieldLayout.FieldDimensions(16.4592, 8.2296));
    smallJson = JsonSerializer.toJson(smallLayout);

    // Large layout (50 tags)
    List<BenchmarkAprilTag> largeTags = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      largeTags.add(
          new BenchmarkAprilTag(
              i,
              new BenchmarkPose3d(
                  new BenchmarkTranslation3d(i, i, i),
                  new BenchmarkRotation3d(new BenchmarkQuaternion(1, 0, 0, 0)))));
    }
    largeLayout =
        new BenchmarkAprilTagFieldLayout(
            largeTags, new BenchmarkAprilTagFieldLayout.FieldDimensions(16.4592, 8.2296));
    largeJson = JsonSerializer.toJson(largeLayout);

    jacksonMapper = new ObjectMapper();
    avajeJsonb = Jsonb.builder().build();

    // Verify all can work with small layout
    try {
      String jacksonJson = jacksonMapper.writeValueAsString(smallLayout);
      BenchmarkAprilTagFieldLayout jacksonResult =
          jacksonMapper.readValue(jacksonJson, BenchmarkAprilTagFieldLayout.class);
      if (!smallLayout.equals(jacksonResult)) {
        throw new RuntimeException("Jackson verification failed: objects not equal");
      }

      String fastjson2Json = JSON.toJSONString(smallLayout);
      BenchmarkAprilTagFieldLayout fastjson2Result =
          JSON.parseObject(fastjson2Json, BenchmarkAprilTagFieldLayout.class);
      if (!smallLayout.equals(fastjson2Result)) {
        throw new RuntimeException("Fastjson2 verification failed: objects not equal");
      }

      String avajeJson = avajeJsonb.toJson(smallLayout);
      BenchmarkAprilTagFieldLayout avajeResult =
          avajeJsonb.type(BenchmarkAprilTagFieldLayout.class).fromJson(avajeJson);
      if (!smallLayout.equals(avajeResult)) {
        throw new RuntimeException("Avaje verification failed: objects not equal");
      }

      BenchmarkAprilTagFieldLayout wpiutilResult =
          JsonDeserializer.deserialize(smallJson, BenchmarkAprilTagFieldLayout.class);
      if (!smallLayout.equals(wpiutilResult)) {
        throw new RuntimeException("wpiutil verification failed: objects not equal");
      }

    } catch (Exception e) {
      throw new RuntimeException("One of the JSON libraries failed verification", e);
    }
  }

  private BenchmarkAprilTagFieldLayout getLayout() {
    return "small".equals(size) ? smallLayout : largeLayout;
  }

  private String getJson() {
    return "small".equals(size) ? smallJson : largeJson;
  }

  // --- WPIUtil Benchmarks ---

  @Benchmark
  public String serializeWpiUtil() {
    return JsonSerializer.toJson(getLayout());
  }

  @Benchmark
  public BenchmarkAprilTagFieldLayout deserializeWpiUtil() {
    return JsonDeserializer.deserialize(getJson(), BenchmarkAprilTagFieldLayout.class);
  }

  // --- Jackson Benchmarks ---

  @Benchmark
  public String serializeJackson() throws JsonProcessingException {
    return jacksonMapper.writeValueAsString(getLayout());
  }

  @Benchmark
  public BenchmarkAprilTagFieldLayout deserializeJackson() throws IOException {
    return jacksonMapper.readValue(getJson(), BenchmarkAprilTagFieldLayout.class);
  }

  // --- Fastjson2 Benchmarks ---

  @Benchmark
  public String serializeFastjson2() {
    return JSON.toJSONString(getLayout());
  }

  @Benchmark
  public BenchmarkAprilTagFieldLayout deserializeFastjson2() {
    return JSON.parseObject(getJson(), BenchmarkAprilTagFieldLayout.class);
  }

  // --- Avaje Benchmarks ---

  @Benchmark
  public String serializeAvaje() {
    return avajeJsonb.toJson(getLayout());
  }

  @Benchmark
  public BenchmarkAprilTagFieldLayout deserializeAvaje() {
    return avajeJsonb.type(BenchmarkAprilTagFieldLayout.class).fromJson(getJson());
  }

  // --- Startup Benchmarks ---
  // We use a separate state to measure the very first call.
  // Each trial is a fresh fork to measure "startup" of the library.

  @State(Scope.Thread)
  public static class StartupState {
    public BenchmarkAprilTagFieldLayout layout;
    public String json;

    @Setup(Level.Trial)
    public void setup() {
      List<BenchmarkAprilTag> tags =
          List.of(
              new BenchmarkAprilTag(
                  1,
                  new BenchmarkPose3d(
                      new BenchmarkTranslation3d(1, 2, 3),
                      new BenchmarkRotation3d(new BenchmarkQuaternion(1, 0, 0, 0)))));
      layout =
          new BenchmarkAprilTagFieldLayout(
              tags, new BenchmarkAprilTagFieldLayout.FieldDimensions(16.4592, 8.2296));
      json = JsonSerializer.toJson(layout);
    }
  }

  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Warmup(iterations = 0)
  @Measurement(iterations = 1)
  @Fork(10)
  public Object startupWpiUtil(StartupState state) {
    return JsonDeserializer.deserialize(state.json, BenchmarkAprilTagFieldLayout.class);
  }

  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Warmup(iterations = 0)
  @Measurement(iterations = 1)
  @Fork(10)
  public Object startupJackson(StartupState state) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.reader().forType(BenchmarkAprilTagFieldLayout.class).readValue(state.json);
  }

  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Warmup(iterations = 0)
  @Measurement(iterations = 1)
  @Fork(10)
  public Object startupFastjson2(StartupState state) {
    return JSON.parseObject(state.json, BenchmarkAprilTagFieldLayout.class);
  }

  @Benchmark
  @BenchmarkMode(Mode.SingleShotTime)
  @OutputTimeUnit(TimeUnit.MILLISECONDS)
  @Warmup(iterations = 0)
  @Measurement(iterations = 1)
  @Fork(10)
  public Object startupAvaje(StartupState state) {
    Jsonb jsonb = Jsonb.builder().build();
    return jsonb.type(BenchmarkAprilTagFieldLayout.class).fromJson(state.json);
  }
}
