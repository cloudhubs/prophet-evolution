package edu.university.ecs.lab.intermediate.create.services;

import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.config.models.InputRepository;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.File;
import java.util.Objects;

/** Service for cloning remote repositories to the local file system. */
@Data
@AllArgsConstructor
public class GitCloneService {
  /** The absolute root path to clone the repositories to */
  private final InputConfig inputConfig;

  /**
   * This method clones a remote repository to the local file system. Postcondition: the repository
   * has been cloned to the local file system.
   *
   * @param inputRepository repository representation from the config file
   * @throws Exception if Git clone failed
   */
  public void cloneRemote(InputRepository inputRepository, String baseCommit) throws Exception {

    String relativeClonePath = inputConfig.getLocalPath(inputRepository);
    FullCimetUtils.microservicePaths.add(relativeClonePath);

    ProcessBuilder processBuilder =
        new ProcessBuilder("git", "clone", inputRepository.getRepoUrl(), relativeClonePath);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();

    int exitCode = process.waitFor();

    if (exitCode < 400) {
      System.out.println("Git clone of " + inputRepository.getRepoUrl() + " successful ");

      if (Objects.isNull(baseCommit)) {
        baseCommit = "HEAD";
      }
      // TODO if commit is null then don't do reset?
      processBuilder = new ProcessBuilder("git", "reset", "--hard", baseCommit);
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
  }
}
