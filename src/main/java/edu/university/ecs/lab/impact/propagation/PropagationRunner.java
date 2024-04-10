package edu.university.ecs.lab.impact.propagation;

import edu.university.ecs.lab.common.services.LoggerManager;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.nio.file.Path;

public class PropagationRunner {

    public static void main(String[] args) throws IOException {
        LoggerManager.log(Level.INFO, "Starting up PropagationRunner!");
//        if (args.length < 2) {
//          System.err.println("Invalid # of args, 2 expected: <path/to/old/intermediate-json>  <path/to/delta>");
//          return;
//        }

        PropagationManager propagationManager
                = new PropagationManager(Path.of("out/rest-extraction-output-[1712114343517].json").toAbsolutePath().toString(),Path.of("out/delta-changes-[1712114367907].json").toAbsolutePath().toString());

        propagationManager.identify();
    }
}
