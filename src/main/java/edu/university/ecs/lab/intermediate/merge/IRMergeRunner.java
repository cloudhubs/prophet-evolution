//package edu.university.ecs.lab.intermediate.merge;
//
//import edu.university.ecs.lab.common.models.MsModel;
//import edu.university.ecs.lab.common.utils.JsonConvertUtils;
//import edu.university.ecs.lab.common.writers.MsJsonWriter;
//import edu.university.ecs.lab.delta.models.Delta;
//import edu.university.ecs.lab.common.models.MsSystem;
//import edu.university.ecs.lab.intermediate.merge.services.MergeService;
//import edu.university.ecs.lab.common.utils.IRParserUtils;
//
//import javax.json.JsonObject;
//import java.io.File;
//import java.io.IOException;
//import java.nio.file.Path;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//public class IRMergeRunner {
//
//  public static void main(String[] args) throws IOException {
////    if (args.length < 2) {
////      System.err.println(
////          "Invalid # of args, 2 expected: <path/to/intermediate-json> <path/to/delta-json>");
////      return;
////    }
//
//    MergeService mergeService = new MergeService();
//
//    MsSystem msSystem = IRParserUtils.parseIRSystem(Path.of("out/rest-extraction-output-[1711537197491].json").toAbsolutePath().toString());
//    Map<String, MsModel> msModelMap = msSystem.getServiceMap();
//    List<Delta> deltas = IRParserUtils.parseDelta(Path.of("out/delta-changes-[1711537261044].json").toAbsolutePath().toString());
//
//    // iterate through delta changes
//    for (Delta delta : deltas) {
//      String localPath = delta.getLocalPath();
//      String msId;
//
//      int serviceNdx = localPath.indexOf("-service");
//
//      // todo: generalize better in the future
//      if (serviceNdx >= 0) {
//        msId = localPath.substring(0, serviceNdx + 8);
//        msId = msId.substring(msId.lastIndexOf("/") + 1);
//      } else {
//        msId = localPath;
//      }
//
//      // check change type
//      switch (delta.getChangeType()) {
//        case "ADD":
//          msModelMap.put(msId, mergeService.addFiles(msId, msModelMap, delta));
//          break;
//        case "DELETE":
//          mergeService.removeFiles(msId, msModelMap, delta);
//          break;
//        case "MODIFY":
//          MsModel modifyModel = mergeService.modifyFiles(msId, msModelMap, delta);
//          if (Objects.isNull(modifyModel)) {
//            continue;
//          }
//
//          msModelMap.put(msId, mergeService.modifyFiles(msId, msModelMap, delta));
//          break;
//        default:
//          break;
//      }
//    }
//
//    // increment system version
//    String systemName = msSystem.getSystemName();
//    String version = mergeService.incrementVersion(msSystem.getVersion());
//
//    // save new system representation
//    writeNewIntermediate(systemName, version, msModelMap);
//  }
//
//  private static void writeNewIntermediate(
//      String systemname, String version, Map<String, MsModel> msModelMap) throws IOException {
//    JsonObject jout = JsonConvertUtils.constructJsonMsSystem(systemname, version, msModelMap);
//
//    String outputPath = System.getProperty("user.dir") + File.separator + "out";
//
//    String outputName =
//        outputPath + File.separator + "rest-extraction-new-[" + (new Date()).getTime() + "].json";
//
//    MsJsonWriter.writeJsonToFile(jout, outputName);
//    System.out.println("Successfully wrote updated extraction to: \"" + outputName + "\"");
//  }
//
//
//
//}
