package edu.university.ecs.lab.delta.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.api.FetchCommand;
import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.FileTreeIterator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

/** Utility class for fetching differences between local and remote git repositories. */
public class GitFetchUtils {
  /** Private constructor to prevent instantiation */
  private GitFetchUtils() {}

  /** The base URL for the GitHub API */
  private static final String GITHUB_API_URL = "https://api.github.com/repos/";

  /**
   * Establish a local endpoint for the given repository path.
   *
   * @param path the path to the repository
   * @return the repository object
   * @throws IOException if an I/O error occurs
   */
  public static Repository establishLocalEndpoint(String path) throws IOException {
    File localRepoDir = new File(path);

    return new FileRepositoryBuilder().setGitDir(new File(localRepoDir, ".git")).build();
  }

  /**
   * Fetch the differences between the local repository and the remote repository.
   *
   * @param repo the repository object established by {@link #establishLocalEndpoint(String)}
   * @param branch the branch name to compare to the local repository
   * @return the list of differences
   * @throws Exception as generated from {@link FetchCommand#call()} or {@link DiffCommand#call()}
   */
  public static List<DiffEntry> fetchRemoteDifferences(Repository repo, String branch) throws Exception {
    try (Git git = new Git(repo)) {
      // fetch latest changes from remote
      git.fetch().call();

      try (ObjectReader reader = repo.newObjectReader()) {
        // get the difference between local main and origin/main
        return git.diff()
            .setOldTree(prepareLocalTreeParser(repo))
            .setNewTree(
                prepareRemoteTreeParser(
                    reader, repo, "refs/remotes/origin/" + branch)) // current local branch
            .call();
      }
    }
  }

  /**
   * Prepare the tree parser for the given repository and git branch reference.
   *
   * @param reader the jgit reader
   * @param repo the jgit repository object
   * @param ref the reference to the repository branch
   * @return the canonical tree parser
   * @throws IOException if an I/O error occurs from parsing the tree
   */
  private static CanonicalTreeParser prepareRemoteTreeParser(
      ObjectReader reader, Repository repo, String ref) throws IOException {
    try (RevWalk walk = new RevWalk(reader)) {
      Ref head = repo.exactRef(ref);
      RevCommit commit = repo.parseCommit(head.getObjectId());
      RevTree tree = walk.parseTree(commit.getTree().getId());

      CanonicalTreeParser treeParser = new CanonicalTreeParser();
      try (ObjectReader newReader = walk.getObjectReader()) {
        treeParser.reset(newReader, tree.getId());
      }

      walk.dispose();
      return treeParser;
    }
  }

  private static AbstractTreeIterator prepareLocalTreeParser(Repository repo) {
    return new FileTreeIterator(repo);
  }
}
