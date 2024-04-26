package edu.university.ecs.lab.common.models.enums;

public enum ErrorCodes {
    SUCCESS,
    GENERAL_ERROR,
    BAD_IR_WRITE,
    /** Error in which the {@link edu.university.ecs.lab.intermediate.create.services.IRExtractionService} could not create a local directory
     * to clone files to as specified by the config file {@link edu.university.ecs.lab.common.config.models.InputConfig}, by default this file is "config.json"*/
    COULD_NOT_CREATE_DIRECTORY,
    /** Failure to clone a git repository in {@link edu.university.ecs.lab.intermediate.create.services.GitCloneService} */
    GIT_CLONE_FAILED,
    /** Failure to scan a microservice system files during IR Extraction */
    MS_SCAN_FAIL,
    /** Incorrect command line arguments */
    BAD_ARGS,
    /** Failure to write to file in intermediate representation extraction {@link edu.university.ecs.lab.intermediate.create.services.IRExtractionService}*/
    IR_FILE_WRITE_FAIL,
    /** Failure to extract IR during delta */
    DELTA_EXTRACT_FAIL
}
