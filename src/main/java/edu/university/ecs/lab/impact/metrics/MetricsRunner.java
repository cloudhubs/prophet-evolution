package edu.university.ecs.lab.impact.metrics;

import edu.university.ecs.lab.impact.metrics.services.MetricsService;

import java.io.IOException;

public class MetricsRunner {

  public static void main(String[] args) throws IOException {
    args =
        new String[] {
          "./out/rest-extraction-output-[1714448356019].json",
          "./out/rest-extraction-new-[1714450087175].json",
          "./out/delta-changes-[1714448475461].json"
        };
    if (args.length < 3) {
      System.err.println(
          "Invalid # of args, 2 expected: <path/to/old/intermediate-json>"
              + " <path/to/new/intermediate-json> <path/to/delta>");
      return;
    }

    MetricsService metricsService =
        new MetricsService(args[0], args[1], args[2]);

    System.out.println(metricsService.generateSystemMetrics());
  }
}
