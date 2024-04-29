package edu.university.ecs.lab.common.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import edu.university.ecs.lab.common.config.models.InputConfig;
import java.io.FileNotFoundException;
import java.io.FileReader;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.BAD_CONFIG;

/**
 * Utility class for reading and validating the input config file
 */
public class ConfigUtil {

  /** Prevent instantiation */
  private ConfigUtil() {}

  /**
   * Validate the input config file
   *
   * @param configPath path to the input config file
   * @return the input config as an object
   */
  public static InputConfig validateConfig(String configPath) {
    InputConfig inputConfig = readConfig(configPath);

    if (inputConfig.getClonePath() == null) {
      System.err.println("Config file requires attribute \"clonePath\"");
      System.exit(BAD_CONFIG.ordinal());
    } else if (inputConfig.getOutputPath() == null) {
      System.err.println("Config file requires attribute \"outputPath\"");
      System.exit(BAD_CONFIG.ordinal());
    } else if (inputConfig.getRepositories() == null) {
      System.err.println("Config file requires attribute \"repositories\"");
      System.exit(BAD_CONFIG.ordinal());
    }

    // TODO ? Add in more necessary params of input config
    // TODO validate that clonePath and outputPath are valid RELATIVE directories starting with "./" from the working directory

    return inputConfig;
  }

  /**
   * Read the input config and return InputConfig object
   *
   * @param configPath path to the input config file
   * @return InputConfig object
   */
  public static InputConfig readConfig(String configPath) {
    JsonReader jsonReader = null;
    try {
      jsonReader = new JsonReader(new FileReader(configPath));
    } catch (FileNotFoundException e) {
      System.err.println("Config file not found: " + configPath);
      System.exit(BAD_CONFIG.ordinal());
    }

    Gson gson = new Gson();

    return gson.fromJson(jsonReader, InputConfig.class);
  }
}
