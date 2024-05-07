package edu.university.ecs.lab.full;

import edu.university.ecs.lab.IncrementalCimetRunner;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import edu.university.ecs.lab.common.utils.FullCimetUtils;
import edu.university.ecs.lab.intermediate.create.IRExtraction;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FullIRConstructionTest {

  private static final String inputConfigPath = "config-full-test.json";
  private static final String baseBranch = "main";
  private static final String baseCommit = "7874340529b1f7094c8c6682e0dc1c37c8033ac2";
  private static final String endCommit = "13f93fae687efebe8b931b58b953fc4a2c2df222";

  private static final Logger LOGGER = Logger.getLogger(FullIRConstructionTest.class.getName());

  static {
    try {
      // This block configure the logger with handler and formatter
      FileHandler fh = new FileHandler("MyLogFile.log");
      LOGGER.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void fullIRComparisonTest() {
    ObjectMapper mapper = new ObjectMapper();

    JsonNode json1 = null, json2 = null;

    // Run IncrementalCimetRunner
    try {
      IncrementalCimetRunner.main(
          new String[] {inputConfigPath, baseBranch, baseCommit, endCommit});
      json1 = mapper.readTree(new File(FullCimetUtils.pathToNewIR));
    } catch (Exception e) {
      LOGGER.severe("Error in IncrementalCimetRunner: " + e.getMessage());
      assert false;
    }

    // Run IR Extraction
    try {
      IRExtraction.main(new String[] {inputConfigPath, baseBranch, endCommit});
      json2 = mapper.readTree(new File(FullCimetUtils.pathToIR));
    } catch (Exception e) {
      LOGGER.severe("Error in IR Extraction: " + e.getMessage());
      assert false;
    }

    // Compare the JSON objects
    boolean isEqual = json1.equals(json2);

    assertTrue(isEqual);
  }
}
