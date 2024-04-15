//package edu.university.ecs.lab.report;
//
//import edu.university.ecs.lab.common.models.MsModel;
//import edu.university.ecs.lab.intermediate.merge.models.Delta;
//import edu.university.ecs.lab.intermediate.merge.models.MsSystem;
//import edu.university.ecs.lab.intermediate.merge.utils.IRParserUtils;
//import freemarker.template.Configuration;
//import freemarker.template.Template;
//import freemarker.template.TemplateException;
//import freemarker.template.TemplateExceptionHandler;
//
//import java.io.*;
//import java.time.LocalDateTime;
//import java.util.*;
//
//public class ReportRunner {
//  private static Configuration cfg;
//
//  private static final int FREEMARKER_CONFIG_ERROR = 2;
//  private static final int IO_EXCEPTION = 3;
//  private static final int TEMPLATE_PROCESS_ERROR = 4;
//
//  public static void main(String[] args) throws IOException {
//    if (args.length < 2) {
//      System.err.println(
//          "Invalid # of args, 2 expected: <path/to/intermediate-json> <path/to/delta-json>");
//      return;
//    }
//
//    configureFreemarker();
//
//    String intermediatePath = args[0];
//    String deltaPath = args[1];
//
//    MsSystem system = IRParserUtils.parseIRSystem(intermediatePath);
//    Map<MsModel, List<Delta>> msChangeMap = getChangeMap(system, deltaPath);
//
//    /* Create a data-model */
//    Map<String, Object> root = new HashMap<>();
//
//    root.put("msName", system.getSystemName());
//    root.put("baseVersion", system.getVersion());
//    root.put("dateTime", String.valueOf(LocalDateTime.now()));
//
//    // TODO after rico changes fix this
//    root.put("branch1", "main");
//    root.put("commit1", "commit1");
//    root.put("branch2", "service-change-branch");
//    root.put("commit2", "commit2");
//
//    root.put("services", msChangeMap);
//
//    /* Get the template (uses cache internally) */
//    Template template = null;
//    try {
//      template = cfg.getTemplate("report.ftl");
//      /* Merge data-model with template */
//      Writer out = new FileWriter("./out/report-[" + new Date().getTime() + "].html");
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
//  private static Map<MsModel, List<Delta>> getChangeMap(MsSystem system, String deltaPath)
//      throws IOException {
//    Map<String, MsModel> msModelMap = system.getServiceMap();
//    List<Delta> deltas = IRParserUtils.parseDelta(deltaPath);
//
//    Map<MsModel, List<Delta>> msChangeMap = new HashMap<>();
//
//    // Construct change map by mapping the delta changes by the local path to the MsModel local path
//    for (Delta delta : deltas) {
//      String localPath = delta.getLocalPath();
//      String msId;
//      int serviceNdx = localPath.indexOf("-service");
//      if (serviceNdx >= 0) {
//        msId = localPath.substring(0, serviceNdx + 8);
//        msId = msId.substring(msId.lastIndexOf("/") + 1);
//      } else {
//        msId = localPath;
//      }
//
//      // Add delta to list for the given MsModel if the list exists already, otherwise create a new
//      // map entry
//      MsModel msModel = msModelMap.get(msId);
//      if (msChangeMap.containsKey(msModel)) {
//        msChangeMap.get(msModel).add(delta);
//      } else {
//        List<Delta> deltaList = new ArrayList<>();
//        deltaList.add(delta);
//        msChangeMap.put(msModel, deltaList);
//      }
//    }
//    return msChangeMap;
//  }
//
//  private static void configureFreemarker() {
//    try {
//      cfg = new Configuration(Configuration.VERSION_2_3_32);
//      cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
//      // Recommended settings for new projects:
//      cfg.setDefaultEncoding("UTF-8");
//      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
//      cfg.setLogTemplateExceptions(false);
//      cfg.setFallbackOnNullLoopVariable(false);
//      cfg.setWrapUncheckedExceptions(true);
//      cfg.setSQLDateAndTimeTimeZone(TimeZone.getDefault());
//    } catch (IOException e) {
//      System.err.println("Error configuring Freemarker: " + e.getMessage());
//      e.printStackTrace();
//      System.exit(FREEMARKER_CONFIG_ERROR);
//    }
//  }
//}
