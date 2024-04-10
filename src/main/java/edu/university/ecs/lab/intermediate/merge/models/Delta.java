package edu.university.ecs.lab.intermediate.merge.models;

import com.google.gson.annotations.SerializedName;
import edu.university.ecs.lab.common.models.JClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Delta {
  private String localPath;
  private String changeType;
  private String commitId;
  private String msName;

  @SerializedName("changes")
  private JClass change;
}
