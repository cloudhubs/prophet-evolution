package edu.university.ecs.lab.delta.services;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.common.writers.MsJsonWriter;
import edu.university.ecs.lab.delta.utils.DeltaComparisonUtils;
import edu.university.ecs.lab.delta.utils.GitFetchUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import javax.json.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.DELTA_EXTRACTION_FAIL;

/**
 * Service for extracting the differences between a local and remote repository and generating delta
 */
public class DeltaExtractionService {

  /** The branch to compare to */
  private final String branch;

  /** The list of paths to the repositories */
  private final String[] paths;

  /**
   * Constructor for the delta extraction service.
   *
   * @param branch the branch to compare to
   * @param paths the list of paths to the repositories
   */
  public DeltaExtractionService(String branch, String[] paths) {
    this.branch = branch;
    this.paths = paths;
  }

  /**
   * Top level generate the delta between the local and remote repository.
   */
  public void generateDelta() {
    // iterate through each repository path
    for (String path : paths) {
        try (Repository localRepo = GitFetchUtils.establishLocalEndpoint(path)) {
            // point to local repository

            // extract remote differences with local
            List<DiffEntry> differences = GitFetchUtils.fetchRemoteDifferences(localRepo, branch);

            // process/write differences to delta output
            this.processDifferences(differences, path);

        } catch (Exception e) {
            System.err.println("Error extracting delta: " + e.getMessage());
            System.exit(DELTA_EXTRACTION_FAIL.ordinal());
        }
    }
  }

  /**
   * Process the differences between the local and remote repository and write the differences to a
   * file. Differences can be generated from {@link GitFetchUtils#fetchRemoteDifferences(Repository, String)}
   *
   * @param path the path to the local repo
   * @param diffEntries the list of differences extracted
   * @throws IOException if an I/O error occurs
   * @throws InterruptedException if an I/O error occurs
   */
  public void processDifferences(List<DiffEntry> diffEntries, String path)
      throws IOException, InterruptedException {

    // Set local repo to latest commit
    advanceLocalRepo(path);

    // Lists for changed objects aka files
    List<JsonObject> controllers = new ArrayList<>();
    List<JsonObject> services = new ArrayList<>();
    List<JsonObject> dtos = new ArrayList<>();
    List<JsonObject> repositories = new ArrayList<>();
    List<JsonObject> entities = new ArrayList<>();

    // Lists for present files, helps remove duplicates and keep latest changes
    List<String> controllersPaths = new ArrayList<>();
    List<String> servicesPaths = new ArrayList<>();
    List<String> dtosPaths = new ArrayList<>();
    List<String> repositoriesPaths = new ArrayList<>();
    List<String> entitiesPaths = new ArrayList<>();

    // process each difference
    for (DiffEntry entry : diffEntries) {
      // skip non-Java files but also include deleted files
      if (!entry.getNewPath().endsWith(".java") && !entry.getNewPath().equals("/dev/null")) {
        continue;
      }

      System.out.println("Extracting changes from: " + path);
      String oldPath = path + "/" + entry.getOldPath();
      String newPath = path + "/" + entry.getNewPath();

      // If the new path is null, it is deleted, and we use old path to parse classtype, otherwise
      // we use newpath
      String localPath =
          "./" + (entry.getNewPath().equals("/dev/null") ? entry.getOldPath() : entry.getNewPath());
      File file = new File(entry.getNewPath().equals("/dev/null") ? oldPath : newPath);

      if (file.getName().contains("Controller")) {

        if(controllersPaths.contains(localPath)) {
          removeItemFromList(controllers, localPath);
        } else {
          controllersPaths.add(localPath);
        }

        controllers.add(
            constructObjectFromDelta(
                ClassRole.CONTROLLER,
                getDeltaChanges(entry, localPath, path),
                entry,
                localPath));

      } else if (file.getName().contains("Service")) {

        if(servicesPaths.contains(localPath)) {
          removeItemFromList(services, localPath);
        } else {
          servicesPaths.add(localPath);
        }

        services.add(
            constructObjectFromDelta(
                ClassRole.SERVICE,
                getDeltaChanges(entry, localPath, path),
                entry,
                localPath));
      } else if (file.getName().toLowerCase().contains("dto")) {

        if(dtosPaths.contains(localPath)) {
          removeItemFromList(dtos, localPath);
        } else {
          dtosPaths.add(localPath);
        }

        dtos.add(
            constructObjectFromDelta(
                ClassRole.DTO,
                getDeltaChanges(entry, localPath, path),
                entry,
                localPath));
      } else if (file.getName().contains("Repository")) {

        if(repositoriesPaths.contains(localPath)) {
          removeItemFromList(repositories, localPath);
        } else {
          repositoriesPaths.add(localPath);
        }

        repositories.add(
            constructObjectFromDelta(
                ClassRole.REPOSITORY,
                getDeltaChanges(entry, localPath, path),
                entry,
                localPath));
      } else if (file.getParent().toLowerCase().contains("entity")
          || file.getParent().toLowerCase().contains("model")) {

        if(entitiesPaths.contains(localPath)) {
          removeItemFromList(entities, localPath);
        } else {
          entitiesPaths.add(localPath);
        }

        entities.add(
            constructObjectFromDelta(
                ClassRole.ENTITY,
                getDeltaChanges(entry, localPath, path),
                entry,
                localPath));
      }

      System.out.println(
          "Change impact of type " + entry.getChangeType() + " detected in " + entry.getNewPath());
    }

    // write differences to output file
    JsonObjectBuilder finalOutputBuilder = Json.createObjectBuilder();
    finalOutputBuilder.add("controllers", convertListToJsonArray(controllers));
    finalOutputBuilder.add("services", convertListToJsonArray(services));
    finalOutputBuilder.add("repositories", convertListToJsonArray(repositories));
    finalOutputBuilder.add("dtos", convertListToJsonArray(dtos));
    finalOutputBuilder.add("entities", convertListToJsonArray(entities));

    String outputName = "./out/delta-changes-[" + (new Date()).getTime() + "].json";
    // TODO move to runner
    FullCimetUtils.pathToDelta = outputName;

    MsJsonWriter.writeJsonToFile(finalOutputBuilder.build(), outputName);

    System.out.println("Delta extracted: " + outputName);
  }

  private static JsonObject getDeltaChanges(
          DiffEntry entry, String localPath, String rootPath) {
    File classFile = new File(rootPath + localPath.substring(1));
    switch (entry.getChangeType()) {
      case MODIFY:
      case RENAME:
      case ADD:
        return DeltaComparisonUtils.extractDeltaChanges(classFile);
      case COPY:
      case DELETE:
        break;
      default:
        break;
    }

    return JsonValue.EMPTY_JSON_OBJECT;
  }

  private static JsonObject constructObjectFromDelta(
      ClassRole classRole, JsonObject deltaChanges, DiffEntry entry, String path) {
    JsonObjectBuilder jout = Json.createObjectBuilder();
    String msName =
        (entry.getNewPath().equals("/dev/null")
            ? entry.getOldPath().substring(0, entry.getOldPath().indexOf('/'))
            : entry.getNewPath().substring(0, entry.getNewPath().indexOf('/')));
    jout.add("localPath", path);
    jout.add("changeType", entry.getChangeType().name());
    jout.add("commitId", entry.getNewId().name());
    if (classRole == ClassRole.CONTROLLER) {
      jout.add("cChange", deltaChanges);

    } else if (classRole == ClassRole.SERVICE) {
      jout.add("sChange", deltaChanges);

    } else {
      jout.add("changes", deltaChanges);
    }
    jout.add("msName", msName);

    return jout.build();
  }

  private static void advanceLocalRepo(String path) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("git", "reset", "--hard", "origin/main");
    processBuilder.directory(new File(Path.of(path).toAbsolutePath().toString()));
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    int exitCode = process.waitFor();
  }

  private static JsonArray convertListToJsonArray(List<JsonObject> jsonObjectList) {
    JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
    for (JsonObject jsonObject : jsonObjectList) {
      arrayBuilder.add(jsonObject);
    }
    return arrayBuilder.build();
  }

  private void removeItemFromList(List<JsonObject> jsonObjectList, String localPath) {
    for(JsonObject jsonObject : jsonObjectList) {
      if(jsonObject.get("localPath").toString().equals(localPath)) {
        jsonObjectList.remove(jsonObject);
        return;
      }
    }
  }
}
