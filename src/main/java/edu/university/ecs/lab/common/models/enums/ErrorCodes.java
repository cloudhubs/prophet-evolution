package edu.university.ecs.lab.common.models.enums;

public enum ErrorCodes {
  SUCCESS("Success!"),
  GENERAL_ERROR,
  /** Error in which a directory could not be created */
  COULD_NOT_CREATE_DIRECTORY("Could not create directory"),
  /**
   * Failure to clone a git repository in {@link
   * edu.university.ecs.lab.intermediate.create.services.GitCloneService}
   */
  GIT_CLONE_FAILED("Failed to clone git repository"),

  /** Incorrect command line arguments */
  BAD_ARGS,
  /**
   * Failure to write to file in intermediate representation extraction {@link
   * edu.university.ecs.lab.intermediate.create.services.IRExtractionService}
   */
  JSON_FILE_WRITE_ERROR,
  /** Failure to extract original IR */
  IR_EXTRACTION_FAIL,
  /** Failure to extract IR during delta */
  DELTA_EXTRACTION_FAIL,
  /** Failure to perform merge service */
  MERGE_FAIL,
  /** Failure to perform metric service */
  METRIC_FAIL,
  /** Failure to perform report service */
  REPORT_FAIL,
  /**
   * Issue generating freemarker report from template in {@link
   * edu.university.ecs.lab.report.ReportService}
   */
  TEMPLATE_PROCESS_ERROR,
  /** Failure to set up freemarker template configuration in {@link } */
  FREEMARKER_CONFIG_ERROR,
  /** Error due to malformed {@link edu.university.ecs.lab.common.config.models.InputConfig} file */
  BAD_CONFIG,
  /**
   * Error due to missing microservices in {@link
   * edu.university.ecs.lab.intermediate.create.services.IRExtractionService}
   */
  NO_MICROSERVICES(
      "No microservices were detected in intermediate representation extraction. "
          + "Check your config file to make sure that the repositories are correct and"
          + "contain the correct structure for the microservices to be detected."),
  ;

  private final String message;

  public String getMessage() {
    return message;
  }

  ErrorCodes() {
    this.message = "";
  }

  ErrorCodes(String message) {
    this.message = message;
  }
}
