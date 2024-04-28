package edu.university.ecs.lab.common.utils;

import com.google.gson.Gson;
import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.delta.models.SystemChangeDTO;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Utility class for parsing IR and delta files previously created.
 */
public class IRParserUtils {
  /** Gson parser */
  private static final Gson gson = new Gson();

  /**
   * Read in an IR (original or merged/new) file and parse it into a MsSystem object.
   * @param irFileName the file path/name of the IR file to parse
   * @return the parsed MsSystem object
   * @throws IOException if an I/O error occurs
   */
  public static MsSystem parseIRSystem(String irFileName) throws IOException {
    Reader irReader = new FileReader(irFileName);

    MsSystem msSystem = gson.fromJson(irReader, MsSystem.class);
    irReader.close();

    return msSystem;
  }

  /**
   * Read in a delta file and parse it into a SystemChange object.
   * @param deltaFileName the file path/name of the delta file to parse
   * @return the parsed SystemChange object
   * @throws IOException if an I/O error occurs
   */
  public static SystemChange parseSystemChange(String deltaFileName) throws IOException {
    Reader deltaReader = new FileReader(deltaFileName);

    SystemChangeDTO systemChangeDto = gson.fromJson(deltaReader, SystemChangeDTO.class);
    deltaReader.close();

    return systemChangeDto.toSystemChange();
  }
}
