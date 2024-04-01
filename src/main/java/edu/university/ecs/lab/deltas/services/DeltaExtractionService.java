package edu.university.ecs.lab.deltas.services;

import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.ParserUtils;
import edu.university.ecs.lab.common.writers.MsJsonWriter;
import edu.university.ecs.lab.deltas.utils.DeltaComparisonUtils;
import edu.university.ecs.lab.deltas.utils.GitFetchUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Config;
import org.eclipse.jgit.lib.Repository;

import javax.json.*;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

import static edu.university.ecs.lab.intermediate.create.services.RestModelService.scanFileForClassModel;

/**
 * Service for extracting the differences between a local and remote repository and generating delta
 * son.
 */
public class DeltaExtractionService {
  /** The GitFetchUtils object for fetching git differences */
  private final GitFetchUtils gitFetchUtils = new GitFetchUtils();

  /** Service to compare service dependency model to the git differences */
  private final DeltaComparisonUtils comparisonUtils = new DeltaComparisonUtils();

  /**
   * Wrapper of {@link GitFetchUtils#establishLocalEndpoint(String)} a local endpoint for the given
   * repository path.
   *
   * @param path the path to the repository
   * @return the repository object
   * @throws IOException if an I/O error occurs
   */
  public Repository establishLocalEndpoint(String path) throws IOException {
    return gitFetchUtils.establishLocalEndpoint(path);
  }

  /**
   * Wrapper of {@link GitFetchUtils#fetchRemoteDifferences(Repository, String)} fetch the
   * differences between the local repository (established from {@link
   * #establishLocalEndpoint(String)} and remote repository.
   *
   * @param repo the repository object established by {@link #establishLocalEndpoint(String)}
   * @param branch the branch name to compare to the local repository
   * @return the list of differences
   * @throws Exception if an error from {@link GitFetchUtils#fetchRemoteDifferences(Repository,
   *     String)}
   */
  public List<DiffEntry> fetchRemoteDifferences(Repository repo, String branch) throws Exception {
    return gitFetchUtils.fetchRemoteDifferences(repo, branch);
  }

  /**
   * Process the differences between the local and remote repository and write the differences to a
   * file. Differences can be generated from {@link #fetchRemoteDifferences(Repository, String)}
   *
   * @param path the path to the microservice TLD
   * @param repo the repository object established by {@link #establishLocalEndpoint(String)}
   * @param diffEntries the list of differences extracted from {@link
   *     #fetchRemoteDifferences(Repository, String)}
   * @throws IOException if an I/O error occurs
   */
  public void processDifferences(String msPath, Repository repo, List<DiffEntry> diffEntries)
          throws IOException, InterruptedException {

    advanceLocalRepo();

    JsonObjectBuilder finalOutputBuilder = Json.createObjectBuilder();

    // Lists for changed objects aka files
    List<JsonObject> controllers = new ArrayList<>();
    List<JsonObject> services = new ArrayList<>();
    List<JsonObject> dtos = new ArrayList<>();
    List<JsonObject> repositories = new ArrayList<>();
    List<JsonObject> entities = new ArrayList<>();

    // process each difference
    for (DiffEntry entry : diffEntries) {
      // skip non-Java files but also include deleted files
      if (!entry.getNewPath().endsWith(".java") && !entry.getNewPath().equals("/dev/null")) {
        continue;
      }

      // String changeURL = gitFetchUtils.getGithubFileUrl(repo, entry);
      System.out.println("Extracting changes from: " + msPath);
      String oldPath = msPath + "/" + entry.getOldPath();
      String newPath = msPath + "/" + entry.getNewPath();

      JsonObject deltaChanges = JsonValue.EMPTY_JSON_OBJECT;
      JsonObjectBuilder entryBuilder = Json.createObjectBuilder();

      ClassRole classRole = null;
      // If the new path is null, it is a delete and we use old path to parse classtype, otherwise we use newpath
      File file = new File((entry.getNewPath().equals("/dev/null") ? oldPath : newPath));

      entryBuilder.add("localPath", newPath);
      entryBuilder.add("changeType", entry.getChangeType().name());
      entryBuilder.add("commitId", entry.getNewId().name());
      entryBuilder.add("changes", deltaChanges);

      if (file.getName().contains("Controller")) {
        controllers.add(constructObjectFromDelta(getDeltaChanges(entry, file, ClassRole.CONTROLLER), entry, file.getPath()));
      } else if (file.getName().contains("Service")) {
        services.add(constructObjectFromDelta(getDeltaChanges(entry, file, ClassRole.SERVICE), entry, file.getPath()));
      } else if (file.getName().toLowerCase().contains("dto")) {
        dtos.add(constructObjectFromDelta(getDeltaChanges(entry, file, ClassRole.DTO), entry, file.getPath()));
      } else if (file.getName().contains("Repository")) {
        repositories.add(constructObjectFromDelta(getDeltaChanges(entry, file, ClassRole.REPOSITORY), entry, file.getPath()));
      } else if (file.getParent().toLowerCase().contains("entity")
              || file.getParent().toLowerCase().contains("model")) {
        entities.add(constructObjectFromDelta(getDeltaChanges(entry, file, ClassRole.ENTITY), entry, file.getPath()));
      }



      System.out.println("Change impact of type " + entry.getChangeType() + " detected in " + entry.getNewPath());
    }

    // write differences to output file
    finalOutputBuilder.add("controllers", convertListToJsonArray(controllers));
    finalOutputBuilder.add("services", convertListToJsonArray(services));
    finalOutputBuilder.add("repositories", convertListToJsonArray(repositories));
    finalOutputBuilder.add("dtos", convertListToJsonArray(dtos));
    finalOutputBuilder.add("entities", convertListToJsonArray(entities));

    String outputName = "./out/delta-changes-[" + (new Date()).getTime() + "].json";
    MsJsonWriter.writeJsonToFile(finalOutputBuilder.build(), outputName);

    System.out.println("Delta extracted: " + outputName);
  }

  private static JsonObject getDeltaChanges(DiffEntry entry, File file, ClassRole classRole) {
    switch (entry.getChangeType()) {
      case MODIFY:
        return DeltaComparisonUtils.extractDeltaChanges(file, classRole);
      case COPY:
      case DELETE:
        break;
      case RENAME:
      case ADD:
        return DeltaComparisonUtils.extractDeltaChanges(file, classRole);
      default:
        break;
    }

    return JsonValue.EMPTY_JSON_OBJECT;
  }

  private static JsonObject constructObjectFromDelta(JsonObject deltaChanges, DiffEntry entry, String path) {
    JsonObjectBuilder jout = Json.createObjectBuilder();
    jout.add("localPath", path);
    jout.add("changeType", entry.getChangeType().name());
    jout.add("commitId", entry.getNewId().name());
    jout.add("changes", deltaChanges);

    return jout.build();
  }

  private static void advanceLocalRepo() throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder("git", "reset", "--hard", "origin/main");
    processBuilder.directory(new File(Path.of("repos/train-ticket-microservices-test/").toAbsolutePath().toString()));
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
}
