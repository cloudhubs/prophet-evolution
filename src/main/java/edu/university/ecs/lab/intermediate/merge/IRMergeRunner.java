package edu.university.ecs.lab.intermediate.merge;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.intermediate.merge.services.MergeService;

import java.io.IOException;

public class IRMergeRunner {

  /**
   * Entry point for the intermediate representation merge process.
   *
   * @param args {@literal </path/to/intermediate-json> </path/to/delta-json> [/path/to/config]}
   *     <compare branch> <compare commit>
   */
  public static void main(String[] args) throws IOException {
    //    args =
    //        new String[] {
    //          "./out/rest-extraction-output-[1714448356019].json",
    //          "./out/delta-changes-[1714448475461].json"
    //        };
    if (args.length != 5) {
      System.err.println(
          "Invalid # of args, 2-3 expected: <path/to/intermediate-json> <path/to/delta-json>"
              + " /path/to/config] <compare branch> <compare commit>");
      return;
    }

    InputConfig inputConfig = ConfigUtil.validateConfig(args[2]);

    MergeService mergeService = new MergeService(args[0], args[1], inputConfig, args[3], args[4]);
    String outputFileName = mergeService.mergeAndWriteToFile();
    FullCimetUtils.pathToNewIR = outputFileName;
  }
}
