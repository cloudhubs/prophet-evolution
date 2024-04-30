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
   * Compares the branch specified in the configuration file to the most recent commit on the remote
   * repository branch name specified in the arguments and generates the delta file.
   *
   * @param args {@literal <branch name> [/path/to/config]}
   */
  public static void main(String[] args) throws Exception {
    args = new String[] {"main"};
    if (args.length < 1 || args.length > 2) {
      System.err.println("Required arguments <branch> [(optional) /path/to/config]");
    }

    String branch = args[0];
    InputConfig inputConfig =
        ConfigUtil.validateConfig((args.length == 2) ? args[1] : "config.json");

    DeltaExtractionService deltaService = new DeltaExtractionService(branch, inputConfig);
    Set<String> outputNames = deltaService.generateDelta();

    FullCimetUtils.pathsToDeltas = outputNames;
  }
}
