package edu.university.ecs.lab.common.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class FullCimetUtils {

  public static String pathToIR;
  public static String pathToDelta;
  public static String pathToNewIR;
  public static Set<String> microservicePaths = new HashSet<>();

  public static String baseBranch = "";
  public static String baseCommit = "";

  // TODO make output dir based on config
  public static String getDeltaOutputName(String branch, String compareCommit) {
    String outputName =
        "./out/delta-changes-["
            + FullCimetUtils.baseBranch
            + "-"
            + (!FullCimetUtils.baseCommit.isEmpty()
                ? FullCimetUtils.baseCommit.substring(0, 7)
                : "base")
            + "-to-"
            + branch
            + "-"
            + compareCommit.substring(0, 7)
            + "].json";
    return outputName;
  }
}
