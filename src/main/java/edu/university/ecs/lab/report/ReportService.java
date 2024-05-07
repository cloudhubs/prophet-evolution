package edu.university.ecs.lab.report;

import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.common.utils.IRParserUtils;
import edu.university.ecs.lab.impact.metrics.services.MetricsService;
import edu.university.ecs.lab.impact.models.SystemMetrics;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.FREEMARKER_CONFIG_ERROR;
import static edu.university.ecs.lab.common.models.enums.ErrorCodes.TEMPLATE_PROCESS_ERROR;
import static edu.university.ecs.lab.common.utils.FullCimetUtils.getShortCommit;

/** To use this class, simply call the constructor and then run generateReport() */
public class ReportService {

  private static final String TEMPLATE_NAME = "report.ftl";
  private static Configuration config;

  static {
    configureFreemarker();
  }

  /** Destination branch (branch where we are merging INTO, usually main/master) */
  private final String baseBranch;

  /** Destination commit (commit where we are merging INTO, on baseBranch) */
  private final String baseCommit;

  /** Source branch (branch where pull request is being made FROM)_ */
  private final String compareBranch;

  /** Source commit (commit where pull request is being made FROM, on compareBranch) */
  private final String compareCommit;

  /** The path to the original system IR */
  private final String intermediatePath;

  /** The service for generating metrics */
  private final MetricsService metricsService;

  /** The base path for output files from config */
  private final String outputBasePath;

  /**
   * Constructor for ReportService
   *
   * @param outputBasePath base path to output files (output directory from config)
   * @param baseBranch branch merging into, usually main/master
   * @param baseCommit commit merging into, on baseBranch
   * @param compareBranch base comparing from, usually feature branch
   * @param compareCommit commit comparing from, on compareBranch
   * @param intermediatePath path to the original system IR
   * @param newIntermediatePath path to the new system IR
   * @throws NullPointerException if either path is null
   */
  ReportService(
      String outputBasePath,
      String baseBranch,
      String baseCommit,
      String compareBranch,
      String compareCommit,
      String intermediatePath,
      String newIntermediatePath,
      String deltaPath)
      throws NullPointerException, IOException {
    this.outputBasePath = Objects.requireNonNull(outputBasePath);
    this.intermediatePath = Objects.requireNonNull(intermediatePath);
    this.baseBranch = baseBranch;
    this.compareBranch = compareBranch;
    this.baseCommit = baseCommit;
    this.compareCommit = compareCommit;
    this.metricsService = new MetricsService(intermediatePath, newIntermediatePath, deltaPath);
  }

  /** Generate freemarker report, should be put into /out by default */
  public void generateReport() throws IOException {
    /* Create a data-model */
    Map<String, Object> root = new HashMap<>();
    MsSystem system = IRParserUtils.parseIRSystem(intermediatePath);

    /* Base System Information */
    root.put("msName", system.getSystemName());
    root.put("baseVersion", system.getVersion());

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
    root.put("dateTime", LocalDateTime.now().format(formatter));

    root.put("branch1", baseBranch);
    root.put("commit1", getShortCommit(baseCommit));
    root.put("branch2", compareBranch);
    root.put("commit2", getShortCommit(compareCommit));

    /* Metrics */
    SystemMetrics systemMetrics = metricsService.generateSystemMetrics();

    root.put("systemMetrics", systemMetrics);

    /* Get the template (uses cache internally) */
    Template template = null;
    try {
      template = config.getTemplate(TEMPLATE_NAME);
      /* Merge data-model with template */
      Writer out = new FileWriter(outputBasePath + "/" + getReportFileName());
      template.process(root, out);
      out.close();
    } catch (IOException | TemplateException e) {
      System.err.println("Error processing template: " + e.getMessage());
      System.exit(TEMPLATE_PROCESS_ERROR.ordinal());
    }
  }

  /**
   * Logic for naming the report file, this can be changed as preferred
   *
   * @return name of the generated file
   */
  private String getReportFileName() {
    return "report-[" + new Date().getTime() + "].html";
  }

  /** Configure Freemarker settings, should happen only once per application run */
  private static void configureFreemarker() {
    try {
      config = new Configuration(Configuration.VERSION_2_3_32);
      config.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
      // Recommended settings for new projects:
      config.setDefaultEncoding("UTF-8");
      config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      config.setLogTemplateExceptions(false);
      config.setFallbackOnNullLoopVariable(false);
      config.setWrapUncheckedExceptions(true);
      config.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
    } catch (IOException e) {
      System.err.println("Error configuring Freemarker: " + e.getMessage());
      System.exit(FREEMARKER_CONFIG_ERROR.ordinal());
    }
  }
}
