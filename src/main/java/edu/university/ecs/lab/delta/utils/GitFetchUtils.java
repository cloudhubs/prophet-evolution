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
  /** The base URL for the GitHub API */
  private static final String GITHUB_API_URL = "https://api.github.com/repos/";

  /** The GSON object for JSON parsing */
  private static final Gson gson = new Gson();

  /**
   * Establish a local endpoint for the given repository path.
   *
   * @param path the path to the repository
   * @return the repository object
   * @throws IOException if an I/O error occurs
   */
  public Repository establishLocalEndpoint(String path) throws IOException {
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
  public List<DiffEntry> fetchRemoteDifferences(Repository repo, String branch) throws Exception {
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
  private CanonicalTreeParser prepareRemoteTreeParser(
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

  /**
   * Used to get the url of a given file where the difference entry originated from
   *
   * @param repo the repository object established by {@link #establishLocalEndpoint(String)}
   * @param entry the difference entry to find the file for
   * @return the GitHub file URL where the given difference entry originated from
   */
  public String getGithubFileUrl(Repository repo, DiffEntry entry) {
    Config config = repo.getConfig();
    String remoteUrl = config.getString("remote", "origin", "url");
    String[] urlSegments = remoteUrl.split("/");

    // extract owner/repo name
    String owner = urlSegments[urlSegments.length - 2];
    String repoName = urlSegments[urlSegments.length - 1].replace(".git", "");

    return GITHUB_API_URL + owner + "/" + repoName + "/contents/" + entry.getNewPath();
  }

  /**
   * Fetch file via {@link GitFetchUtils#fetchJsonFromUrl(String)} and decode the file content from
   * the given URL.
   *
   * @param url the URL to fetch the file content from
   * @return the decoded file content
   * @throws IOException if an I/O error occurs
   */
  public String fetchAndDecodeFile(String url) throws IOException {
    String changeData = fetchJsonFromUrl(url);
    JsonObject changeDetails = gson.fromJson(changeData, JsonObject.class);
    String encodedContent =
        changeDetails.getAsJsonObject().get("content").getAsString().replaceAll("\\s", "");
    return new String(Base64.getDecoder().decode(encodedContent), StandardCharsets.UTF_8);
  }

  /**
   * GET data JSON from the given URL.
   *
   * @param url the URL to fetch JSON from
   * @return the JSON string
   * @throws IOException if an I/O error occurs from {@link org.apache.http.client.HttpClient}
   */
  private String fetchJsonFromUrl(String url) throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpGet request = new HttpGet(url);
      //      request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + ACCESS_TOKEN);
      //      request.setHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json");

      return EntityUtils.toString(httpClient.execute(request).getEntity());
    }
  }
}
