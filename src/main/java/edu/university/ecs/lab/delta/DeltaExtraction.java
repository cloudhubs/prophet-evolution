package edu.university.ecs.lab.delta;

import edu.university.ecs.lab.delta.services.DeltaExtractionService;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.Repository;

import java.util.*;

/** Service for extracting the differences between a local and remote repository. */
public class DeltaExtraction {
  /**
   * main method entry point to delta extraction
   *
   * @param args [branch] [list containing /path/to/repo(s)]
   */
  public static void main(String[] args) throws Exception {
    DeltaExtractionService deltaService = new DeltaExtractionService();
    if (args.length < 2) {
      System.err.println("Required arguments <branch> <list of paths...>");
    }

    String branch = args[0];
    String[] paths = Arrays.copyOfRange(args, 1, args.length);

    // iterate through each repository path
    for (String path : paths) {
      // point to local repository
      Repository localRepo = deltaService.establishLocalEndpoint(path);

      // extract remote differences with local
      List<DiffEntry> differences = deltaService.fetchRemoteDifferences(localRepo, branch);

      // process/write differences to delta output
      deltaService.processDifferences(path, localRepo, differences);

      // close repository after use
      localRepo.close();
    }
  }
}
