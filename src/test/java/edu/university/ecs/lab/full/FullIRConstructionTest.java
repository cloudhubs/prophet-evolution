package edu.university.ecs.lab.full;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.delta.DeltaExtraction;
import edu.university.ecs.lab.intermediate.create.IRExtraction;

import java.io.IOException;
import java.util.Collections;

import edu.university.ecs.lab.intermediate.merge.IRMergeRunner;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class FullIRConstructionTest {

  private final static String inputConfigPath = "config-full-test.json";
  private final static String baseBranch = "main";
  private final static String baseCommit = "7874340529b1f7094c8c6682e0dc1c37c8033ac2";
//  private final static String baseCommit = "7abcaf2ea0cdd04d68bb7909799bc37302b254b8";

  private static final Logger LOGGER = Logger.getLogger(FullIRConstructionTest.class.getName());

  static {
    try {
      // This block configure the logger with handler and formatter
      FileHandler fh = new FileHandler("MyLogFile.log");
      LOGGER.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @BeforeAll
  public static void setUp() {
    // Run IR Extraction on the base commit
    String[] IRArgs = {inputConfigPath, baseBranch, baseCommit};
    try {
      IRExtraction.main(IRArgs);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Test
  public void fullIRConstructionTest() throws Exception {

    InputConfig inputConfig = ConfigUtil.validateConfig(inputConfigPath);
    String repoPath = inputConfig.getClonePath() + File.separator + "train-ticket-microservices";

    Repository repo = new RepositoryBuilder().setGitDir(new File(repoPath, ".git")).build();

    try (Git git = new Git(repo)) {

      // Fetch updates from the remote repository
      git.fetch().setRemote("origin").call();

      // Get all commits from the remote branch
      Iterable<RevCommit> commitsIterable = git.log().add(repo.resolve("refs/remotes/origin/" + baseBranch)).call();

      // Convert Iterable to List
      List<RevCommit> commitsList = StreamSupport.stream(commitsIterable.spliterator(), false).collect(Collectors.toList());

      // Reverse the list
      Collections.reverse(commitsList);

      int count = 0;
      boolean isBaseCommit = false;
      boolean isMerged = false;

      for (RevCommit commit : commitsList) {

        if (!isBaseCommit && commit.getName().equals(baseCommit)) {
          LOGGER.info("Beginning IR Extraction: " + baseCommit);

          // Run IR Extraction on the base commit
          String[] IRArgs = {inputConfigPath, baseBranch, baseCommit};
          IRExtraction.main(IRArgs);

          isBaseCommit = true;
        }
        else if (isBaseCommit) {
          LOGGER.info("Beginning Delta Extraction: " + commit.getName());


          // Run Delta Extraction on next commit
          String[] deltaArgs = {baseBranch, commit.getName(), inputConfigPath};
          DeltaExtraction.main(deltaArgs);


          if (isBaseCommit && !isMerged) {
            LOGGER.info("Beginning Merge IR Extraction: " + commit.getName());

            // Run IR Merge Runner on IR and Merge
            String[] mergeArgs = {FullCimetUtils.pathToIR, FullCimetUtils.pathToDelta, inputConfigPath, baseBranch, commit.getName()};
            IRMergeRunner.main(mergeArgs);

            isMerged = true;
          }
          else {
            LOGGER.info("Beginning Merge IR Extraction: " + commit.getName());

            String[] mergeArgs = {FullCimetUtils.pathToNewIR, FullCimetUtils.pathToDelta, inputConfigPath, baseBranch, commit.getName()};
            IRMergeRunner.main(mergeArgs);
          }

        }



        count++;
      }

      System.out.println(FullCimetUtils.pathToNewIR);

      System.out.println("Total commits: " + count);
    }
  }
}
