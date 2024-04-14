package edu.university.ecs.lab.common.config;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.config.models.InputRepository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class ConfigUtil {
  /** Exit code: invalid config path */
  private static final int BAD_CONFIG = 2;

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
      System.exit(BAD_CONFIG);
    } else if (inputConfig.getOutputPath() == null) {
      System.err.println("Config file requires attribute \"outputPath\"");
      System.exit(BAD_CONFIG);
    } else if (inputConfig.getRepositories() == null) {
      System.err.println("Config file requires attribute \"repositories\"");
      System.exit(BAD_CONFIG);
    }

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
      System.exit(BAD_CONFIG);
    }

    Gson gson = new Gson();

    return gson.fromJson(jsonReader, InputConfig.class);
  }


  /**
   * This method parses a repository url and extracts the repository name
   *
   * @param repositoryUrl the repository url to parsing
   * @return the repository name
   */
  public static String getRepositoryName(String repositoryUrl) {
//    System.out.println("Extracting repo from url: " + repositoryUrl);

    // Extract repository name from the URL
    int lastSlashIndex = repositoryUrl.lastIndexOf('/');
    int lastDotIndex = repositoryUrl.lastIndexOf('.');
    return repositoryUrl.substring(lastSlashIndex + 1, lastDotIndex);
  }

  public static String getRepositoryClonePath(InputConfig inputConfig, InputRepository inputRepository) {
    return inputConfig.getClonePath() + File.separator + getRepositoryName(inputRepository.getRepoUrl());
  }
}
