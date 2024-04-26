package edu.university.ecs.lab.intermediate.create.services;

import edu.university.ecs.lab.common.config.ConfigUtil;
import edu.university.ecs.lab.common.config.models.InputConfig;
import edu.university.ecs.lab.common.config.models.InputRepository;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.Microservice;
import edu.university.ecs.lab.common.utils.ObjectToJsonUtils;
import edu.university.ecs.lab.common.writers.MsJsonWriter;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.*;

/**
 * Top-level service for extracting intermediate representation from remote repositories. Methods
 * are allowed to exit the program with an error code if an error occurs.
 */
public class IRExtractionService {
    /** The input configuration file, defaults to config.json */
    private final InputConfig config;
    /** Relative path to clone repositories to, default: "./repos", specified in {@link InputConfig} */
    private final String clonePath;
    private final GitCloneService gitCloneService;

    public IRExtractionService(InputConfig config) {
        this.config = config;
        clonePath = config.getClonePath();
        gitCloneService = new GitCloneService(config);
    }

    /**
     * Intermediate extraction runner, generates IR from remote repository and writes to file.
     * @return the name of the output file
     */
    public String generateSystemIntermediate() {
        // Clone remote repositories and scan through each cloned repo to extract endpoints
        Map<String, Microservice> msDataMap = this.cloneAndScanServices();

        if (msDataMap == null) {
            System.out.println("No microservices were detected in intermediate representation extraction. " +
                    "Check your config file to make sure that the repositories are correct and" +
                    "contain the correct structure for the microservices to be detected.");
            System.exit(0);
        }

        // Scan through each endpoint to update rest call destinations
        this.updateCallDestinations(msDataMap);

        //  Write each service and endpoints to IR
        try {
            return this.writeToFile(msDataMap);
        } catch (IOException e) {
            System.err.println("Error writing to IR json: " + e.getMessage());
            System.exit(JSON_FILE_WRITE_ERROR.ordinal());
            return null;
        }
    }

    /**
     * Clone remote repositories and scan through each local repo and extract endpoints/calls
     *
     * @return a map of services and their endpoints
     */
    public Map<String, Microservice> cloneAndScanServices() {
        Map<String, Microservice> msModelMap = new HashMap<>();

        this.validateOrCreateLocalDirectory(clonePath);

        // For each git repository in the config file, clone the repository and scan through the downloaded repo
        // for the microservices as per the structure in the input config file
        for (InputRepository inputRepository : config.getRepositories()) {
            // Clone the remote repository
            try {
                gitCloneService.cloneRemote(inputRepository);
            } catch (Exception e) {
                System.err.println("Error cloning repository: " + e.getMessage());
                System.exit(GIT_CLONE_FAILED.ordinal());
            }

            // Get list of paths to local microservices as "./cloneDir/.../msName"
            List<String> microservicePaths = getMicroservicePaths(inputRepository);

            // Scan through each local repo and extract endpoints/calls
            for (String msPath : microservicePaths) {
                // Bulk of the work extracting the microservice from the cloned files
                Microservice model =
                        RestModelService.recursivelyScanFiles(clonePath, msPath.substring(clonePath.length()));
                if (Objects.isNull(model)) {
                    System.err.println("Error scanning repository: " + msPath);
                    System.exit(IR_EXTRACTION_FAIL.ordinal());
                }

                model.setCommit(inputRepository.getBaseCommit());

                // Remove clonePath from path
                String path = msPath;
                if (msPath.contains(clonePath) && msPath.length() > clonePath.length() + 1) {
                    path = msPath.substring(clonePath.length() + 1);
                }

                msModelMap.put(path, model);
            }
        }

        return msModelMap;
    }


    /**
     * Create local directory to clone files to ("./repos" by default) as specified in the config file.
     * <div><br/>Exits the program if the directory cannot be created.</div>
     *
     * <div><br/>Post condition: Directory is created.</div>
     */
    private void validateOrCreateLocalDirectory(String dirPath) {
        File cloneDir = new File(dirPath);
        if (!cloneDir.exists()) {
            if (cloneDir.mkdirs()) {
                System.out.println("Successfully created \"" + dirPath + "\" directory.");
            } else {
                System.err.println("Could not create \"" + dirPath + "\" directory");
                System.exit(COULD_NOT_CREATE_DIRECTORY.ordinal());
            }
        }
    }

    /**
     * This method gets the paths to the microservices in the repository based on the config file structure
     * @param inputRepository repository representation from the config file
     * @return list of paths to the microservices in the repository
     */
    public List<String> getMicroservicePaths(InputRepository inputRepository) {
        List<String> microservicePaths = new ArrayList<>();

        // Path "./clonePath/repoName"
        String relativeClonePath = ConfigUtil.getRepositoryClonePath(config, inputRepository);

        if (Objects.nonNull(inputRepository.getPaths()) && inputRepository.getPaths().length > 0) {
            for (String subPath : inputRepository.getPaths()) {
                String path;

                if (subPath.substring(0, 1).equals(File.separator)) {
                    // Case: Subpath starts with a separator ("/ts-service")
                    path = relativeClonePath + subPath;
                } else {
                    // Case: Subpath does not start with a separator ("ts-service")
                    path = relativeClonePath + File.separator + subPath;
                }

                File f = new File(path);

                if (f.isDirectory()) {
                    microservicePaths.add(path);
                } else {
                    System.err.println("Invalid path given in config file, given path \"" + subPath + "\"  is not a directory, skipping service: " + path);
                }
            }
        } else {
            // Case: Single microservice in the repository as the top-level directory (msName = repoName)
            microservicePaths.add(relativeClonePath);
        }

        return microservicePaths;
    }

    /**
     * Iterate over extracted services and link together rest calls to their destination endpoints
     *
     * @param msMap a map of service to their information
     */
    private void updateCallDestinations(Map<String, Microservice> msMap) {
        for (Microservice src : msMap.values()) {
            for (JController controller : src.getControllers()) {
                for (Microservice dest : msMap.values()) {
                    if (dest != src) {
                        for (JService service : dest.getServices()) {
                            service.getRestCalls().forEach(restCall -> {
                                if (controller.getEndpoints().stream().anyMatch(e ->
                                        e.getUrl().equals(restCall.getApi()))) {
                                    // TODO change logic to be not reliant on files
                                    restCall.setDestFile(controller.getClassPath());
                                }
                            });
                        }
                    }
                }
            }
        }
    }

    /**
     * Write each service and endpoints to intermediate representation
     *
     * @param msMap a map of service to their information
     */
    private String writeToFile(Map<String, Microservice> msMap) throws IOException {
        String outputName = this.getOutputFileName();

        validateOrCreateLocalDirectory(config.getOutputPath());

        // TODO implement version number
        JsonObject jout = ObjectToJsonUtils.buildSystem(config.getSystemName(), "0.0.1", msMap);
        MsJsonWriter.writeJsonToFile(jout, outputName);

        System.out.println("Successfully wrote rest extraction to: \"" + outputName + "\"");
        return outputName;
    }

    /**
     * Get name of output file for the IR
     *
     * @return the output file name
     */
    private String getOutputFileName() {
        return config.getOutputPath()
                + File.separator
                + "rest-extraction-output-["
                + (new Date()).getTime()
                + "].json";
    }
}
