package edu.university.ecs.lab.common.utils;

import com.google.gson.Gson;
import edu.university.ecs.lab.intermediate.merge.models.Delta;
import edu.university.ecs.lab.intermediate.merge.models.MsSystem;
import edu.university.ecs.lab.intermediate.merge.models.SystemChange;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class IRParserUtils {
  private static final Gson gson = new Gson();

  public static MsSystem parseIRSystem(String irFileName) throws IOException {
    Reader irReader = new FileReader(irFileName);

    MsSystem msSystem = gson.fromJson(irReader, MsSystem.class);
    irReader.close();

    return msSystem;
  }

  public static SystemChange parseDelta(String deltaFileName) throws IOException {
    Reader deltaReader = new FileReader(deltaFileName);

    SystemChange systemChange = gson.fromJson(deltaReader, SystemChange.class);
    deltaReader.close();

    return systemChange;
  }
}
