//package edu.university.ecs.lab.report;
//
//import edu.university.ecs.lab.common.models.MsSystem;
//import edu.university.ecs.lab.common.utils.IRParserUtils;
//import edu.university.ecs.lab.delta.models.SystemChange;
//import edu.university.ecs.lab.impact.metrics.services.MetricsService;
//import edu.university.ecs.lab.impact.models.SystemMetrics;
//import edu.university.ecs.lab.impact.models.change.Metric;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import freemarker.template.TemplateExceptionHandler;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Writer;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.*;
//
///** To use this class, simply call the constructor and then run generateReport() */
//public class ReportService {
//
//  private static final int FREEMARKER_CONFIG_ERROR = 2;
//  private static final int IO_EXCEPTION = 3;
//  private static final int TEMPLATE_PROCESS_ERROR = 4;
//  private static final String OUTPUT_PATH = "./out/";
//  private static final String TEMPLATE_NAME = "report.ftl";
//  private static Configuration config;
//
//  static {
//    configureFreemarker();
//  }
//
//  /** Destination branch (branch where we are merging INTO, usually main/master) */
//  private final String baseBranch;
//  private final String baseCommit;
//
//
//  /** Source branch (branch where pull request is being made FROM)_ */
//  private final String compareBranch;
//  private final String compareCommit;
//
//  /** The path to the original system IR */
//  private final String intermediatePath;
//
//  /** The path to the system delta */
//  private final String deltaPath;
//
//  private final MetricsService metricsService;
//
//  /**
//   * Constructor for ReportService
//   *
//   * @param baseBranch branch merging into, usually main/master
//   * @param baseCommit commit merging into, on baseBranch
//   * @param compareBranch base comparing from, usually feature branch
//   * @param compareCommit commit comparing from, on compareBranch
//   * @param intermediatePath path to the original system IR
//   * @param deltaPath path to the system delta
//   * @throws NullPointerException if either path is null
//   */
//  ReportService(
//      String baseBranch,
//      String baseCommit,
//      String compareBranch,
//      String compareCommit,
//      String intermediatePath,
//      String deltaPath)
//      throws NullPointerException, IOException {
//    this.intermediatePath = Objects.requireNonNull(intermediatePath);
//    this.deltaPath = Objects.requireNonNull(deltaPath);
//    this.metricsService = new MetricsService(intermediatePath, deltaPath);
//    this.baseBranch = baseBranch;
//    this.compareBranch = compareBranch;
//    this.baseCommit = baseCommit;
//    this.compareCommit = baseCommit;
//
//  }
//
//  /** Generate freemarker report, should be put into /out by default */
//  public void generateReport() throws IOException {
//    /* Create a data-model */
//    Map<String, Object> root = new HashMap<>();
//    MsSystem system = IRParserUtils.parseIRSystem(intermediatePath);
//    SystemChange deltas = IRParserUtils.parseSystemChange(deltaPath);
//
//    /* Base System Information */
//    root.put("msName", system.getSystemName());
//    root.put("baseVersion", system.getVersion());
//
//    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");
//    root.put("dateTime", LocalDateTime.now().format(formatter));
//
//    root.put("branch1", baseBranch);
//    root.put("commit1", baseCommit);
//    root.put("branch2", compareBranch);
//    root.put("commit2", compareCommit);
//
//    /* Metrics */
//    SystemMetrics systemMetrics = metricsService.generateSystemMetrics();
//
//    root.put("systemMetrics", systemMetrics);
//
//    /* Get the template (uses cache internally) */
//    Template template = null;
//    try {
//      template = config.getTemplate(TEMPLATE_NAME);
//      /* Merge data-model with template */
//      Writer out = new FileWriter(OUTPUT_PATH + getReportFileName());
//      template.process(root, out);
//      out.close();
//    } catch (IOException e) {
//      System.err.println("Error reading template: " + e.getMessage());
//      System.exit(IO_EXCEPTION);
//    } catch (TemplateException e) {
//      System.err.println("Error processing template: " + e.getMessage());
//      System.exit(TEMPLATE_PROCESS_ERROR);
//    }
//  }
//
//  /**
//   * Logic for naming the report file, this can be changed as preferred
//   *
//   * @return name of the generated file
//   */
//  private String getReportFileName() {
//    return "report-[" + new Date().getTime() + "].html";
//  }
//
//  /** Configure Freemarker settings, should happen only once per application run */
//  private static void configureFreemarker() {
//    try {
//      config = new Configuration(Configuration.VERSION_2_3_32);
//      config.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
//      // Recommended settings for new projects:
//      config.setDefaultEncoding("UTF-8");
//      config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//      config.setLogTemplateExceptions(false);
//      config.setFallbackOnNullLoopVariable(false);
//      config.setWrapUncheckedExceptions(true);
//      config.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
//    } catch (IOException e) {
//      System.err.println("Error configuring Freemarker: " + e.getMessage());
//      System.exit(FREEMARKER_CONFIG_ERROR);
//    }
//  }
//}
