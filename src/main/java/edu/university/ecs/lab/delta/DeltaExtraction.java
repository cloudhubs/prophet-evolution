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
    args = new String[]{"main", "./repos/train-ticket-microservices"};
    if (args.length < 2) {
      System.err.println("Required arguments <branch> <list of paths...>");
    }

    String branch = args[0];
    String[] paths = Arrays.copyOfRange(args, 1, args.length);

    DeltaExtractionService deltaService = new DeltaExtractionService(branch, paths);
    deltaService.generateDelta();
  }
}
