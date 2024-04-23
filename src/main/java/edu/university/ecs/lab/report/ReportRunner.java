package edu.university.ecs.lab.report;

import java.io.*;
import java.util.*;

/** Testing for report execution, {@link ReportService} contains all logic */
public class ReportRunner {

  public static void main(String[] args) throws IOException {
    if (args.length != 6) {
      System.err.println(
          "Invalid # of args, 2 expected: <base branch> <base commit> <compare branch> <compare commit>"
              + " <path/to/intermediate-json> <path/to/delta-json>");
      return;
    }

    ReportService reportService = new ReportService(args[0], args[1], args[2], args[3], args[4], args[5]);
    reportService.generateReport();
  }
}
