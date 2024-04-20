package edu.university.ecs.lab.intermediate.create;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.config.models.InputRepository;
import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.common.utils.JsonConvertUtils;
import edu.university.ecs.lab.common.writers.MsJsonWriter;
import edu.university.ecs.lab.intermediate.create.services.GitCloneService;
import edu.university.ecs.lab.intermediate.create.services.RestModelService;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * IntermediateExtraction is the main entry point for the intermediate extraction process.
 *
 * <p>The IR extraction process is responsible for cloning remote services, scanning through each
 * local repo and extracting rest endpoints/calls, and writing each service and endpoints to
 * intermediate representation.
 *
 * <p>
 */
public class IRExtraction {
  /** Exit code: error writing IR to json */
  private static final int BAD_IR_WRITE = 3;

  /** system property for user directory */
  // private static final String SYS_USER_DIR = "user.dir";

  /**
   * Main method entry point to intermediate extraction
   *
   * @param args (optional) /path/to/config/file, defaults to config.json in the project directory.
   */
  public static void main(String[] args) throws Exception {
    // Get input config
    String jsonFilePath = (args.length == 1) ? args[0] : "config.json";
    InputConfig inputConfig = ConfigUtil.validateConfig(jsonFilePath);

    // Clone remote repositories and scan through each cloned repo to extract endpoints
    Map<String, Microservice> msDataMap = cloneAndScanServices(inputConfig);

    assert msDataMap != null;

    // Scan through each endpoint to update rest call destinations
    updateCallDestinations(msDataMap);

    //  Write each service and endpoints to IR
    try {
      writeToIntermediateRepresentation(inputConfig, msDataMap);
    } catch (IOException e) {
      System.err.println("Error writing to IR json: " + e.getMessage());
      System.exit(BAD_IR_WRITE);
    }
  }

  /**
   * Write each service and endpoints to intermediate representation
   *
   * @param inputConfig the config file object
   * @param msEndpointsMap a map of service to their information
   */
  private static void writeToIntermediateRepresentation(
      InputConfig inputConfig, Map<String, Microservice> msEndpointsMap) throws IOException {

    String outputPath = inputConfig.getOutputPath();

    File outputDir = new File(outputPath);

    if (!outputDir.exists()) {
      if (outputDir.mkdirs()) {
        System.out.println("Successfully created output directory.");
      } else {
        System.err.println("Failed to create output directory.");
        return;
      }
    }

    JsonObject jout =
        JsonConvertUtils.buildSystem(inputConfig.getSystemName(), "0.0.1", msEndpointsMap);

    String outputName =
        outputPath
            + File.separator
            + "rest-extraction-output-["
            + (new Date()).getTime()
            + "].json";

    FullCimetUtils.pathToIR = outputName;

    MsJsonWriter.writeJsonToFile(jout, outputName);
    System.out.println("Successfully wrote rest extraction to: \"" + outputName + "\"");
  }

  /**
   * Clone remote repositories and scan through each local repo and extract endpoints/calls
   *
   * @param inputConfig the input config object
   * @return a map of services and their endpoints
   */
  private static Map<String, Microservice> cloneAndScanServices(InputConfig inputConfig)
      throws Exception {
    Map<String, Microservice> msModelMap = new HashMap<>();

    // Clone remote repositories
    String clonePath = inputConfig.getClonePath();

    File cloneDir = new File(clonePath);
    if (!cloneDir.exists()) {
      if (cloneDir.mkdirs()) {
        System.out.println("Successfully created \"" + clonePath + "\" directory.");
      } else {
        System.err.println("Could not create clone directory");
        return null;
      }
    }
    GitCloneService gitCloneService = new GitCloneService(inputConfig);

    for (InputRepository inputRepository : inputConfig.getRepositories()) {

      Microservice model;

      gitCloneService.cloneRemote(inputRepository);

      List<String> microservicePaths = gitCloneService.getMicroservicePaths(inputRepository);

      // Scan through each local repo and extract endpoints/calls
      for (String msPath : microservicePaths) {
        String path = msPath;

        if (msPath.contains(clonePath) && msPath.length() > clonePath.length() + 1) {
          path = msPath.substring(clonePath.length() + 1);
        }

        model =
            RestModelService.recursivelyScanFiles(clonePath, msPath.substring(clonePath.length()));
        assert model != null;

        model.setCommit(inputRepository.getBaseCommit());
        model.setId(msPath.substring(msPath.lastIndexOf(File.separator) + 1));
        model.setMsPath(msPath);

        msModelMap.put(path, model);
      }
    }

    return msModelMap;
  }

  private static void updateCallDestinations(Map<String, Microservice> msModelMap) {
    for (Microservice cModel : msModelMap.values()) {
      for (JController controller : cModel.getControllers()) {
        for (Microservice sModel : msModelMap.values()) {
          // TODO Temp fix, an api dest cannot be in same micro-service
          if (sModel != cModel) {
            for (JService service : sModel.getServices()) {
              service
                  .getRestCalls()
                  .forEach(
                      restCall -> {
                        if (controller.getEndpoints().stream()
                            .anyMatch(e -> e.getUrl().equals(restCall.getApi()))) {
                          restCall.setDestFile(controller.getClassPath());
                        }
                      });
            }
          }
        }
      }
    }
  }
}
