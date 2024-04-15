package edu.university.ecs.lab.report;

import edu.university.ecs.lab.common.models.MsModel;
import edu.university.ecs.lab.intermediate.merge.models.Delta;
import edu.university.ecs.lab.intermediate.merge.models.MsSystem;
import edu.university.ecs.lab.intermediate.merge.utils.IRParserUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Testing for report execution, {@link ReportService} contains all logic
 */
public class ReportRunner {

  public static void main(String[] args) throws IOException {
    if (args.length < 2) {
      System.err.println(
          "Invalid # of args, 2 expected: <path/to/intermediate-json> <path/to/delta-json>");
      return;
    }

    ReportService reportService = new ReportService(args[0], args[1]);
    reportService.generateReport();
  }
}
