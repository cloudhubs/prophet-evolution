package edu.university.ecs.lab.common.config.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/** Model to represent the configuration JSON file input */
@Getter
@Setter
public class InputConfig {

  /** The name of the system analyzed */
  private String systemName;

  /** The path to write output files from the program to */
  private String outputPath;

  /** The path to write cloned repository files to */
  private String clonePath;

  /** The list of repository objects as indicated by config */
  private List<InputRepository> repositories;
}
