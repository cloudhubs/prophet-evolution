package edu.university.ecs.lab.report;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;

import java.io.*;
import java.util.*;

/** Testing for report execution, {@link ReportService} contains all logic */
public class ReportRunner {

  /**
   * Main method for generating a report by running metrics and report generation.
   *
   * @param args <base branch> <base commit> <compare branch> <compare commit>
   *     <path/to/intermediate-json> <path/to/new-intermediate-json> <path/to/delta-json>
   */
  public static void main(String[] args) throws IOException {
    if (args.length != 8) {
      System.err.println(
          "Invalid # of args, 2 expected: <path/to/config> <base branch> <base commit> <compare branch> <compare"
              + " commit> <path/to/intermediate-json> <path/to/new-intermediate-json>"
              + " <path/to/delta-json>");
      return;
    }

    InputConfig inputConfig = ConfigUtil.validateConfig(args[0]);
    String baseBranch = args[1];
    String baseCommit = args[2];
    String compareBranch = args[3];
    String compareCommit = args[4];
    String intermediatePath = args[5];
    String newIntermediatePath = args[6];
    String deltaPath = args[7];

    ReportService reportService =
        new ReportService(
            inputConfig.getOutputPath(),
            baseBranch,
            baseCommit,
            compareBranch,
            compareCommit,
            intermediatePath,
            newIntermediatePath,
            deltaPath);

    reportService.generateReport();
  }
}
