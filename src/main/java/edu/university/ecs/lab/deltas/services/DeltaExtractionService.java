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
        controllers.add(getDeltaChanges(entry, file, ClassRole.CONTROLLER));
      } else if (file.getName().contains("Service")) {
        services.add(getDeltaChanges(entry, file, ClassRole.SERVICE));
      } else if (file.getName().toLowerCase().contains("dto")) {
        dtos.add(getDeltaChanges(entry, file, ClassRole.DTO));
      } else if (file.getName().contains("Repository")) {
        repositories.add(getDeltaChanges(entry, file, ClassRole.REPOSITORY));
      } else if (file.getParent().toLowerCase().contains("entity")
              || file.getParent().toLowerCase().contains("model")) {
        entities.add(getDeltaChanges(entry, file, ClassRole.ENTITY));
      }



      System.out.println("Change impact of type " + entry.getChangeType() + " detected in " + entry.getNewPath());
    }

    // write differences to output file
    finalOutputBuilder.add("controllers", Json.createArrayBuilder(controllers).build());
    finalOutputBuilder.add("services", Json.createArrayBuilder(services).build());
    finalOutputBuilder.add("repositories", Json.createArrayBuilder(repositories).build());
    finalOutputBuilder.add("dtos", Json.createArrayBuilder(dtos).build());
    finalOutputBuilder.add("entities", Json.createArrayBuilder(entities).build());

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
      case RENAME:
      case ADD:
        return DeltaComparisonUtils.extractDeltaChanges(file, classRole);
      default:
        break;
    }

    return JsonValue.EMPTY_JSON_OBJECT;
  }
}
