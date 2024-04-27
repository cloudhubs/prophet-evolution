package edu.university.ecs.lab.delta.utils;

import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.JsonToObjectUtils;
import edu.university.ecs.lab.common.utils.ObjectToJsonUtils;

import javax.json.*;
import java.io.File;
import java.io.IOException;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.DELTA_EXTRACTION_FAIL;

/** Utility class for comparing differences between two files. */
public class DeltaComparisonUtils {
    /** Private constructor to prevent instantiation */
    private DeltaComparisonUtils() {}

  /**
   * Extract a representation of an JClass from the given classFile
   * and return a Json representing the class object.
   *
   * @param classFile file to scan for differences
   * @return the differences between the two files as a JSON array
   * @throws IOException if an I/O error occurs
   */
  public static JsonObject extractDeltaChanges(File classFile) {
      JClass jClass = null;
      try {
          jClass = JsonToObjectUtils.parseClass(classFile);
      } catch (IOException e) {
        System.err.println("Error parsing class file: " + classFile.getAbsolutePath());
        System.err.println(e.getMessage());
        System.exit(DELTA_EXTRACTION_FAIL.ordinal());
      }

      // TODO debug Austin, if this fails then I messed up :)
      assert jClass != null;
      if (jClass.getClassRole() == ClassRole.CONTROLLER && !(jClass instanceof JController)) {
        throw new RuntimeException("ClassRole is CONTROLLER but class is not JController");
      }
      if (jClass.getClassRole() == ClassRole.SERVICE && !(jClass instanceof JService)) {
        throw new RuntimeException("ClassRole is CONTROLLER but class is not JService");
      }
      // DEBUG ^^^^^ can remove later

        if (jClass.getClassRole() == ClassRole.CONTROLLER) {
            return ObjectToJsonUtils.buildRestController("", (JController) jClass);
        } else if (jClass.getClassRole() == ClassRole.SERVICE) {
            return ObjectToJsonUtils.buildRestService((JService) jClass);
        }

        // TODO implement the rest of the class roles

        return ObjectToJsonUtils.buildJavaClass(jClass);
  }
}
