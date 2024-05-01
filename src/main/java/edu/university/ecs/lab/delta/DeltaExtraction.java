package edu.university.ecs.lab.delta;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.delta.services.DeltaExtractionService;

import java.util.*;

/**
 * Service for extracting the differences between a local and remote repository. TODO: notice how
 * {@link DeltaExtractionService#generateDelta()} returns a set of file names, we should make this
 * all 1 file for the multi-repo case.
 */
public class DeltaExtraction {
  /**
   * Compares the branch specified in the Rest Extraction file to a commit on the remote repository
   * branch name specified in the arguments and generates the delta file.
   *
   * @param args {@literal <compareBranch> <compareCommit> [/path/to/config]}
   */
  public static void main(String[] args) throws Exception {
    if (args.length < 2 || args.length > 3) {
      System.err.println(
          "Required arguments <compareBranch> <compareCommit> [(optional) /path/to/config]");
    }

    String branch = args[0];
    String compareCommit = args[1];
    InputConfig inputConfig =
        ConfigUtil.validateConfig((args.length == 3) ? args[2] : "config.json");

    DeltaExtractionService deltaService =
        new DeltaExtractionService(branch, compareCommit, inputConfig);
    List<String> outputNames = deltaService.generateDelta();

    // TODO make work for multi-repo case
    FullCimetUtils.pathToDelta = outputNames.get(0);
  }
}
