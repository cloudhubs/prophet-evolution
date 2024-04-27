package edu.university.ecs.lab.delta.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.delta.models.enums.ChangeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Delta {
  private String localPath;
  private ChangeType changeType;
  private String commitId;
  private String msId;

  @SerializedName("changes")
  private JClass change;

  private JController cChange;
  private JService sChange;
}
