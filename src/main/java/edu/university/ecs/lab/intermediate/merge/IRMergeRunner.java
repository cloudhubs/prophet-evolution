package edu.university.ecs.lab.intermediate.merge;


import java.io.IOException;


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
