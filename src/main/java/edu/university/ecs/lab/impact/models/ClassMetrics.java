package edu.university.ecs.lab.impact.models;

import edu.university.ecs.lab.common.models.enums.ClassRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassMetrics {

  private ClassRole classRole;

  private int addedClassCount;

  private int removedClassCount;

  private int modifiedClassCount;

  public void incrementAddedClassCount() {
    addedClassCount++;
  }

  public void incrementRemovedClassCount() {
    removedClassCount++;
  }

  public void incrementModifiedClassCount() {
    modifiedClassCount++;
  }

}
