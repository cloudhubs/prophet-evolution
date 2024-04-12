package edu.university.ecs.lab.common.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigUtil {
  /** Exit code: invalid config path */
  private static final int BAD_CONFIG = 2;

  /**
   * Validate the input config file
   *
   * @param jsonFilePath path to the input config file
   * @return the input config as an object
   */
  public static InputConfig validateConfig(String jsonFilePath) {
    JsonReader jsonReader = null;
    try {
      jsonReader = new JsonReader(new FileReader(jsonFilePath));
    } catch (FileNotFoundException e) {
      System.err.println("Config file not found: " + jsonFilePath);
      System.exit(BAD_CONFIG);
    }

    Gson gson = new Gson();
    InputConfig inputConfig = gson.fromJson(jsonReader, InputConfig.class);

    if (inputConfig.getClonePath() == null) {
      System.err.println("Config file requires attribute \"clonePath\"");
      System.exit(BAD_CONFIG);
    } else if (inputConfig.getOutputPath() == null) {
      System.err.println("Config file requires attribute \"outputPath\"");
      System.exit(BAD_CONFIG);
    } else if (inputConfig.getRepositories() == null) {
      System.err.println("Config file requires attribute \"repositories\"");
      System.exit(BAD_CONFIG);
    }

    //TODO ? Add in more necessary params of input config

    return inputConfig;
  }
}
