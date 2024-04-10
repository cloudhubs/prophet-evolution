package edu.university.ecs.lab.delta.models;

import lombok.AllArgsConstructor;
import lombok.Data;

/** Model to represent any and all useful change information for calculation of delta */
@Data
@AllArgsConstructor
public class ChangeInformation {
  /** current locally changed line */
  private String localLine;

  /** remote (base comparison) line */
  private String remoteLine;

  /** affected line number */
  private int lineNumber;
}
