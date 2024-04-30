package edu.university.ecs.lab.common.config.models;

import javassist.NotFoundException;
import lombok.Getter;
import lombok.Setter;

/** Model to represent the microservice object in the configuration JSON file input */
@Getter
public class InputRepository {
  /** The url of the git repository */
  private String repoUrl;

  /** Commit number the service originated from */
  @Setter
  private String baseCommit;

  /** The paths to each microservice TLD in the repository */
  private String[] paths;

  /**
   * This method parses the repository url and extracts the repository name
   *
   * @return the repository name
   */
  public String getName() {
    // Extract repository name from the URL
    int lastSlashIndex = repoUrl.lastIndexOf('/');
    int lastDotIndex = repoUrl.lastIndexOf('.');
    return repoUrl.substring(lastSlashIndex + 1, lastDotIndex);
  }

  /**
   * This method returns the service name from the input ms path.
   * Validates that the given path is within the paths written in the config. Handles
   * case that the microservice is not in a top level directory.
   *
   * @param path the path to the service
   * @return the service name
   */
  public String getServiceNameFromPath(String path) throws NotFoundException {
    // Check path is within paths
    for (String p : paths) {
      if (path.contains(p)) {
        return path.substring(path.lastIndexOf("/") + 1);
      }
    }

    throw new NotFoundException("Path not found in repository paths");
  }
}
