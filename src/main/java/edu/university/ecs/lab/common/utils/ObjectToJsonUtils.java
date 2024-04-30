package edu.university.ecs.lab.common.utils;

import edu.university.ecs.lab.common.models.*;

import javax.json.*;
import java.util.List;

/** Utility class for converting objects to JSON. */
public class ObjectToJsonUtils {
  /** Private constructor to prevent instantiation. */
  private ObjectToJsonUtils() {}

  /**
   * Convert a list of JsonSerializable objects to a JsonArray.
   *
   * @param list the list of JsonSerializable objects to convert
   * @return the JsonArray representation of the list
   */
  public static <T extends JsonSerializable> JsonArray listToJsonArray(List<T> list) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    for (T item : list) {
      jsonArrayBuilder.add(item.toJsonObject());
    }
    return jsonArrayBuilder.build();
  }

  /**
   * Convert a list of JsonSerializable objects to a JsonArray.
   *
   * @param list the list of JsonSerializable objects to convert
   * @return the JsonArray representation of the list
   */
  public static JsonArray stringListToJsonArray(List<String> list) {
    JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();
    for (String item : list) {
      jsonArrayBuilder.add(item);
    }
    return jsonArrayBuilder.build();
  }
}
