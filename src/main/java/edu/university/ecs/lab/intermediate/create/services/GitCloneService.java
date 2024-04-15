package edu.university.ecs.lab.intermediate.create.services;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.config.models.InputRepository;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Service for cloning remote repositories to the local file system. */
@Data
@AllArgsConstructor
public class GitCloneService {
  /** The absolute root path to clone the repositories to */
  private final InputConfig inputConfig;

  /**
   * This method clones a remote repository to the local file system
   *
   * @param inputRepository the repo to be cloned
   * @return list of service paths
   * @throws Exception if Git clone failed
   */
  public void cloneRemote(InputRepository inputRepository) throws Exception {

    String relativeClonePath = ConfigUtil.getRepositoryClonePath(inputConfig, inputRepository);
    ProcessBuilder processBuilder =
        new ProcessBuilder("git", "clone", inputRepository.getRepoUrl(), relativeClonePath);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    int exitCode = process.waitFor();

    if (exitCode < 400) {
      System.out.println("Git clone of " + inputRepository.getRepoUrl() + " successful ");

      if (Objects.isNull(inputRepository.getBaseCommit())) {
        inputRepository.setBaseCommit("HEAD");
      }

      processBuilder =
          new ProcessBuilder("git", "reset", "--hard", inputRepository.getBaseCommit());
      processBuilder.directory(new File(relativeClonePath));
      processBuilder.redirectErrorStream(true);
      process = processBuilder.start();

      exitCode = process.waitFor();

      // TODO exit code not working
      if (exitCode < 400) {
        System.out.println("Git reset of " + inputRepository.getRepoUrl() + " successful ");
      } else {
        throw new Exception(
            "Git reset of "
                + inputRepository.getRepoUrl()
                + " failed with status code: "
                + exitCode);
      }
    } else {
      throw new Exception(
          "Git clone of " + inputRepository.getRepoUrl() + " failed with status code: " + exitCode);
    }

    // output = output.replaceAll("\\\\", "/");

    // add microservices to path

  }

  public List<String> getMicroservicePaths(InputRepository inputRepository) throws Exception {
    List<String> microservicePaths = new ArrayList<>();
    String relativeClonePath = ConfigUtil.getRepositoryClonePath(inputConfig, inputRepository);

    if (Objects.nonNull(inputRepository.getPaths()) && inputRepository.getPaths().length > 0) {
      for (String subPath : inputRepository.getPaths()) {
        String path;

        if (subPath.substring(0, 1).equals(File.separator)) {
          path = relativeClonePath + subPath;
        } else {
          path = relativeClonePath + File.separator + subPath;
        }

        File f = new File(path);

        if (f.isDirectory()) {
          microservicePaths.add(path);
        }
      }
    } else {
      microservicePaths.add(relativeClonePath);
    }

    return microservicePaths;
  }
}
