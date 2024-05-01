package edu.university.ecs.lab.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.SystemChangeDTO;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import static edu.university.ecs.lab.common.models.enums.ErrorCodes.*;

/** Utility class for parsing IR and delta files previously created. */
public class IRParserUtils {
  /** Gson parser */
  private static final Gson gson =
      new GsonBuilder().registerTypeAdapter(Delta.class, Delta.getAdapter()).create();

  /**
   * Read in an IR (original or merged/new) file and parse it into a MsSystem object.
   *
   * @param irFileName the file path/name of the IR file to parse
   * @return the parsed MsSystem object
   * @throws IOException if an I/O error occurs
   */
  public static MsSystem parseIRSystem(String irFileName) {
      MsSystem msSystem = null;
      try {
        Reader irReader = new FileReader(irFileName);
        msSystem = gson.fromJson(irReader, MsSystem.class);
        irReader.close();
      } catch (FileNotFoundException e) {
        System.err.println("IR File not Found: " + irFileName);
        System.exit(FILE_NOT_FOUND.ordinal());
      } catch (IOException e) {
        System.err.println("Error reading IR file: " + irFileName);
        System.exit(IR_EXTRACTION_FAIL.ordinal());
      }

    return msSystem;
  }

  /**
   * Read in a delta file and parse it into a SystemChange object.
   *
   * @param deltaFileName the file path/name of the delta file to parse
   * @return the parsed SystemChange object
   * @throws IOException if an I/O error occurs
   */
  public static SystemChange parseSystemChange(String deltaFileName) {
    SystemChangeDTO systemChangeDto = null;
    try {
      Reader deltaReader = new FileReader(deltaFileName);
      systemChangeDto = gson.fromJson(deltaReader, SystemChangeDTO.class);
      deltaReader.close();
    } catch (FileNotFoundException e) {
      System.err.println("Delta file not Found: " + deltaFileName);
      System.exit(FILE_NOT_FOUND.ordinal());
    } catch (IOException e) {
      System.err.println("Error reading delta file: " + deltaFileName);
      System.exit(DELTA_EXTRACTION_FAIL.ordinal());
    }


    return systemChangeDto.toSystemChange();
  }
}
