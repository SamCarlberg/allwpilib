package edu.wpi.first.wpilib.sendable.schema;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

public class SendableSchemaDeserializer implements JsonDeserializer<SendableSchema> {

  private static final String SENDABLE_TYPE_NAME = "type";
  private static final String SENDABLE_ACTUATOR = "actuator"; // optional (default false)
  private static final String SENDABLE_SAFE_STATE = "hasSafeState"; // optional (default false)
  private static final String SENDABLE_UPDATE_TABLE = "hasUpdateTable"; // optional (default false)
  private static final String SENDABLE_PROPERTIES_NAME = "properties";
  private static final String PROPERTY_TYPE = "type";
  private static final String SET = "set"; // optional (default true)
  private static final String METADATA = "metadata"; // optional (default false)

  private static final Pattern PROPERTY_NAME_PATTERN = Pattern.compile("[a-zA-Z0-9 _]+");

  @Override
  public SendableSchema deserialize(JsonElement json,
                                    Type typeOfT,
                                    JsonDeserializationContext context) throws JsonParseException {
    JsonObject object = json.getAsJsonObject();
    if (!object.has(SENDABLE_TYPE_NAME)) {
      throw new JsonParseException("No sendable type was specified in the schema");
    }
    String type = object.get(SENDABLE_TYPE_NAME).getAsString();
    boolean isActuator =
        object.has(SENDABLE_ACTUATOR) && object.get(SENDABLE_ACTUATOR).getAsBoolean();
    boolean hasSafeState =
        object.has(SENDABLE_SAFE_STATE) && object.get(SENDABLE_SAFE_STATE).getAsBoolean();
    boolean hasUpdateTable =
        object.has(SENDABLE_UPDATE_TABLE) && object.get(SENDABLE_UPDATE_TABLE).getAsBoolean();

    List<SendableProperty> properties = new ArrayList<>();
    JsonObject jsonProperties = object.getAsJsonObject(SENDABLE_PROPERTIES_NAME);

    if (jsonProperties != null) {
      for (String propName : jsonProperties.keySet()) {
        if (!PROPERTY_NAME_PATTERN.matcher(propName).matches()) {
          throw new JsonSyntaxException(
              "Property name contains illegal characters '" + propName + "'");
        }
        JsonObject property = jsonProperties.getAsJsonObject(propName);

        if (!property.has(PROPERTY_TYPE)) {
          throw new JsonParseException(
              "No property type specified for property '" + propName + "'");
        }

        SendablePropertyType propertyType =
            SendablePropertyType.forScheme(property.get(PROPERTY_TYPE).getAsString());
        boolean hasSetter = !property.has(SET) || property.get(SET).getAsBoolean();
        boolean isMetadata = property.has(METADATA) && property.get(METADATA).getAsBoolean();

        SendableProperty sendableProperty =
            new SendableProperty(propName, propertyType, hasSetter, isMetadata); // NOPMD
        properties.add(sendableProperty);
      }
    }

    return new SendableSchema(type, isActuator, hasSafeState, hasUpdateTable, properties);
  }

}
