package edu.university.ecs.lab.intermediate.create;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.intermediate.create.services.IRExtractionService;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.BAD_ARGS;

/**
 * {@link IRExtraction} is the main entry point for the intermediate extraction process, relying on
 * {@link IRExtractionService}.
 *
 * <p>The IR extraction process is responsible for cloning remote services, scanning through each
 * local repo and extracting rest endpoints/calls, and writing each service and endpoints to
 * intermediate representation.
 *
 * <p>
 */
public class IRExtraction {

  /**
   * Intermediate extraction runner, generates IR from remote repository and writes to file.
   *
   * @param args [/path/to/config/file]
   * @apiNote defaults to config.json in the project directory.
   */
  public static void main(String[] args) throws Exception {
    if (args.length > 1) {
      System.err.println("Usage: java -jar IRExtraction.jar [/path/to/config/file]");
      System.exit(BAD_ARGS.ordinal());
    }

    // Get input config
    InputConfig inputConfig =
        ConfigUtil.validateConfig((args.length == 1) ? args[0] : "config.json");

    IRExtractionService irExtractionService = new IRExtractionService(inputConfig);
    String outputFileName = irExtractionService.generateSystemIntermediate();

    // Save the file name for the full system runner
    FullCimetUtils.pathToIR = outputFileName;
  }
}
