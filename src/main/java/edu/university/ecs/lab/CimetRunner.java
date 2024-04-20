package edu.university.ecs.lab;

import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.delta.DeltaExtraction;
import edu.university.ecs.lab.intermediate.create.IRExtraction;
import edu.university.ecs.lab.intermediate.merge.IRMergeRunner;
import edu.university.ecs.lab.report.ReportRunner;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

public class CimetRunner {

  /**
   * Main method for full report
   *
   * @param args /path/to/config/file <base branch> <compare branch>
   */
  public static void main(String[] args) throws Exception {

    if (args.length < 3) {
      System.err.println("Required arguments /path/to/config/file <base branch> <compare branch>");
      return;
    }

    // RUN IR EXTRACTION
    System.out.println("Starting IR Extraction...");
    String[] IRExtractionArgs = Arrays.copyOfRange(args, 0, 1);
    IRExtraction.main(IRExtractionArgs);

    // RUN DELTA
    System.out.println("Starting Delta Extraction...");
    String[] deltaArgs =
        ArrayUtils.addAll(
            Arrays.copyOfRange(args, 2, 3),
            FullCimetUtils.microservicePaths.toArray(new String[0]));
    DeltaExtraction.main(deltaArgs);

    // RUN IR MERGE
    System.out.println("Starting IR Merge...");
    String[] IRMergeArgs = {FullCimetUtils.pathToIR, FullCimetUtils.pathToDelta};
    IRMergeRunner.main(IRMergeArgs);

    // RUN REPORT
    System.out.println("Starting Report Creation...");
    String[] reportArgs = ArrayUtils.addAll(Arrays.copyOfRange(args, 1, 3), IRMergeArgs);
    ReportRunner.main(reportArgs);
  }
}
