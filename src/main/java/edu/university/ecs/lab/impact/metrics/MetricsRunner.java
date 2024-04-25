package edu.university.ecs.lab.impact.metrics;

import edu.university.ecs.lab.impact.metrics.services.MetricsService;

import java.io.IOException;
import java.nio.file.Path;

public class MetricsRunner {

  public static void main(String[] args) throws IOException {
    args = new String[]{"./out/rest-extraction-output-[1713220548219].json", "./out/rest-extraction-new-[1713220834011].json", "./out/delta-changes-[1713220716931].json"};
    if (args.length < 3) {
      System.err.println(
          "Invalid # of args, 2 expected: <path/to/old/intermediate-json>  <path/to/delta>");
      return;
    }

    MetricsService metricsService =
        new MetricsService(
            Path.of(args[0]).toAbsolutePath().toString(),
            Path.of(args[1]).toAbsolutePath().toString(),
            Path.of(args[2]).toAbsolutePath().toString());

    System.out.println(metricsService.generateSystemMetrics());
  }
}
