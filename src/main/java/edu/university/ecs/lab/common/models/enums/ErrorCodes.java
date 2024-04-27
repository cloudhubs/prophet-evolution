package edu.university.ecs.lab.common.models.enums;

public enum ErrorCodes {
    SUCCESS,
    GENERAL_ERROR,
    /** Error in which a directory could not be created */
    COULD_NOT_CREATE_DIRECTORY,
    /** Failure to clone a git repository in {@link edu.university.ecs.lab.intermediate.create.services.GitCloneService} */
    GIT_CLONE_FAILED,

    /** Incorrect command line arguments */
    BAD_ARGS,
    /** Failure to write to file in intermediate representation extraction {@link edu.university.ecs.lab.intermediate.create.services.IRExtractionService}*/
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
    /** Issue generating freemarker report from template in {@link edu.university.ecs.lab.report.ReportService} */
    TEMPLATE_PROCESS_ERROR,
    /** Failure to set up freemarker template configuration in {@link }*/
    FREEMARKER_CONFIG_ERROR

}
