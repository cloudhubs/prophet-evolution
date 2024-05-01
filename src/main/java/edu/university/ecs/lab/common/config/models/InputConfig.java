package edu.university.ecs.lab.common.config.models;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Model to represent the configuration JSON file input */
@Getter
@Setter
public class InputConfig {

  /** The name of the system analyzed */
  private String systemName;

  /** The path to write output files from the program to */
  private String outputPath;

  /** The path to write cloned repository files to */
  private String clonePath;

  /** The list of repository objects as indicated by config */
  private List<InputRepository> repositories;

  /**
   * This method returns a set of all of relative paths to where the repositories will be cloned to
   * on the local file system.
   *
   * @return list of relative paths to the local cloned repos
   */
  public List<String> getLocalPaths() {
    return repositories.stream().map(this::getLocalPath).collect(Collectors.toList());
  }

  /**
   * This method returns the relative local path of a cloned repository as ./clonePath/repoName.
   * This will be a working relative path to the repository on the local file system.
   *
   * @param inputRepository one of the input repositories from the config
   * @return the relative path string where that repository is cloned to
   */
  public String getLocalPath(InputRepository inputRepository) {
    return this.getClonePath() + "/" + inputRepository.getName();
  }

  /**
   * This method gets local paths to each microservice in the repository based on the config file
   * structure.
   *
   * <p>Should only be called AFTER cloning the repository, as it validates the ms directories.
   * These will be working relative paths to each microservice in the repository.
   *
   * @param inputRepository repository representation from the config file
   * @return list of paths to the microservices in the repository .<br>
   *     <strong>Paths will be like: "./clonePath/repoName/.../serviceName"</strong>
   */
  public List<String> getMicroservicePaths(InputRepository inputRepository) {
    List<String> microservicePaths = new ArrayList<>();

    // Path "clonePath/repoName"
    String relativeClonePath = this.getLocalPath(inputRepository);

    // Case: Single microservice in the repository as the top-level directory (msName = repoName)
    if (Objects.isNull(inputRepository.getPaths()) || inputRepository.getPaths().length == 0) {
      microservicePaths.add(relativeClonePath);
      return microservicePaths;
    }

    // Case: Multiple microservices in the repository
    for (String subPath : inputRepository.getPaths()) {

      String path;
      if (subPath.charAt(0) == '/') {
        // Case: Subpath starts with a separator ("/ts-service")
        path = relativeClonePath + subPath;
      } else {
        // Case: Subpath does not start with a separator ("ts-service")
        path = relativeClonePath + "/" + subPath;
      }

      // Validate path is a directory
      File f = new File(path);
      if (f.isDirectory()) {
        microservicePaths.add(path);
      } else {
        System.err.println(
            "Invalid path given in config file, given path \""
                + subPath
                + "\"  is not a directory, skipping service: "
                + path);
      }
    }

    return microservicePaths;
  }

  /**
   * This method will take any file path and return a shortened version as just
   * <strong>"repoName/.../serviceName/.../file.java"</strong> removing the clonePath from the
   * beginning of the path. Will not start with a "./" or a "/".
   *
   * @return the shortened path
   */
  public String getShortPath(String fullPath) {
    return fullPath.substring(fullPath.indexOf(clonePath) + clonePath.length() + 1);
  }
}
