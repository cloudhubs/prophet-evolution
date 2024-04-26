package edu.university.ecs.lab.intermediate.merge;

import edu.university.ecs.lab.common.models.Microservice;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.utils.ObjectToJsonUtils;
import edu.university.ecs.lab.common.writers.MsJsonWriter;
import edu.university.ecs.lab.delta.models.Delta;
import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.delta.models.SystemChange;
import edu.university.ecs.lab.intermediate.merge.services.MergeService;
import edu.university.ecs.lab.common.utils.IRParserUtils;

import javax.json.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IRMergeRunner {

  public static void main(String[] args) throws IOException {
    args = new String[]{"./out/rest-extraction-output-[1714032248580].json", "./out/delta-changes-[1714032300406].json"};
    if (args.length < 2) {
      System.err.println(
          "Invalid # of args, 2 expected: <path/to/intermediate-json> <path/to/delta-json>");
      return;
    }

    MergeService mergeService = new MergeService(args[0], args[1]);
    mergeService.mergeAndWriteToFile();
  }
}
