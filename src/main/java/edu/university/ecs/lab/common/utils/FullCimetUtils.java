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
}
