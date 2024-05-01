package edu.university.ecs.lab.common.utils;

import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class IRParserUtilsTest {

  @Test
  void parseIRSystem() {}

  @Test
  void parseSystemChange() {}

  @Test
  void getClassNameFromLocalPath() {
    assertEquals(
        "classname", IRParserUtils.getClassNameFromLocalPath("some/path/to/classname.java"));
    assertEquals("classname2", IRParserUtils.getClassNameFromLocalPath("/classname2.java"));
  }

  @Test
  void getServiceFromLocalPath() {}
}
