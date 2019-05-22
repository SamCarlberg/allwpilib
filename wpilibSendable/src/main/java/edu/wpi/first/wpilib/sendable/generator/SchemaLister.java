package edu.wpi.first.wpilib.sendable.generator;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import edu.wpi.first.wpilib.sendable.schema.SchemaReader;
import edu.wpi.first.wpilib.sendable.schema.SendableSchema;

public class SchemaLister {

  /**
   * Lists all the available schema resources.
   */
  public List<InputStream> list() {
    return getFilenamesForDirname("/schema")
        .stream()
        .sorted()
        .filter(resourceName -> resourceName.endsWith(".sendable.json"))
        .map(SchemaLister.class::getResourceAsStream)
        .collect(Collectors.toList());
  }

  /**
   * Generates code for each available schema. Generated code is mapped to the schema from which it
   * is generated.
   *
   * @param reader    the reader to use to read the schemas
   * @param generator the code generator to use
   * @return the generated code
   */
  public Map<SendableSchema, String> generate(SchemaReader reader, Generator generator) {
    return list().stream()
        .map(reader::readSchema)
        .collect(Collectors.toMap(Function.identity(), generator::generate));
  }

  /**
   * Lists the names of all the files in the given resource directory.
   *
   * @param directoryName the name of the resource directory, relative to the SchemaLister class
   * @return a list of names of the files in the given resource directory
   */
  public static List<String> getFilenamesForDirname(String directoryName) {
    List<String> filenames = new ArrayList<>();

    URL url = SchemaLister.class.getResource(directoryName);
    if (url != null && url.getProtocol().equals("file")) {
      File dir;
      try {
        dir = Paths.get(url.toURI()).toFile();
      } catch (URISyntaxException impossible) {
        return Collections.emptyList();
      }
      File[] files = dir.listFiles();
      if (files != null) {
        int skip = dir.toString().length() - directoryName.length();
        for (File fileName : files) {
          filenames.add(fileName.toString().substring(skip));
        }
      }
    }
    return filenames;
  }

}
