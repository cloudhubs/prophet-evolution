package edu.fudan.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fdse
 */
public class JsonUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

  private JsonUtils() {
    throw new IllegalStateException("Utility class");
  }

  /** Object to JSON string */
  public static String object2Json(Object obj) {
    String result = null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      result = objectMapper.writeValueAsString(obj);
    } catch (IOException e) {
      JsonUtils.LOGGER.error("[object2Json][writeValueAsString][IOException: {}]", e.getMessage());
    }
    return result;
  }

  public static Map object2Map(Object obj) {
    String object2Json = object2Json(obj);
    Map<?, ?> result = jsonToMap(object2Json);
    return result;
  }

  /** JSON string to Map object */
  public static Map<?, ?> jsonToMap(String json) {
    return json2Object(json, Map.class);
  }

  /** JSON to Object */
  public static <T> T json2Object(String json, Class<T> cls) {
    T result = null;
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      result = objectMapper.readValue(json, cls);
    } catch (NullPointerException e) {
      JsonUtils.LOGGER.error(
          "[json2Object][objectMapper.readValue][NullPointerException: {}]", e.getMessage());
    } catch (IOException e) {
      JsonUtils.LOGGER.error(
          "[json2Object][objectMapper.readValue][IOException: {}]", e.getMessage());
    }

    return result;
  }

  public static <T> T conveterObject(Object srcObject, Class<T> destObjectType) {
    String jsonContent = object2Json(srcObject);
    return json2Object(jsonContent, destObjectType);
  }
}
