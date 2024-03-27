package edu.university.ecs.lab.deltas;

import edu.university.ecs.lab.common.models.MsModel;
import edu.university.ecs.lab.deltas.services.DeltaExtractionService;
import edu.university.ecs.lab.intermediate.merge.models.MsSystem;
import edu.university.ecs.lab.intermediate.merge.utils.IRParserUtils;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/** Service for extracting the differences between a local and remote repository. */
public class DeltaExtraction {
  /**
   * main method entry point to delta extraction
   *
   * @param args command line args list containing /path/to/repo(s)
   */
  public static void main(String[] args) throws Exception {
    DeltaExtractionService deltaService = new DeltaExtractionService();

    MsSystem msSystem = IRParserUtils.parseIRSystem(Path.of("out/rest-extraction-output-[1711537197491].json").toAbsolutePath().toString());
    Map<String, MsModel> msModelMap = msSystem.getServiceMap();
    // iterate through each repository path
    for (String path : List.of(Path.of("repos/train-ticket-microservices-test").toAbsolutePath().toString())) {
      // point to local repository
      Repository localRepo = deltaService.establishLocalEndpoint(path);

      // extract remote differences with local
      List<DiffEntry> differences = deltaService.fetchRemoteDifferences(localRepo, "main");



      // process/write differences to delta output
      deltaService.processDifferences(path, localRepo, differences);

      // close repository after use
      localRepo.close();
    }
  }
}
