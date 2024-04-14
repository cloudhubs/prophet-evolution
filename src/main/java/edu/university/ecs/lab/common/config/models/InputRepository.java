package edu.university.ecs.lab.common.config.models;

import lombok.Getter;
import lombok.Setter;

/** Model to represent the microservice object in the configuration JSON file input */
@Getter
@Setter
public class InputRepository {
  /** The url of the git repository */
  private String repoUrl;

  /** Commit number the service originated from */
  private String baseCommit;

  /** Commit number the service ended at */
  // TODO is this unused? if so remove
  @Deprecated private String endCommit;

  /** The paths to each microservice TLD in the repository */
  private String[] paths;
}
