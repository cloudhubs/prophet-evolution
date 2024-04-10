package edu.university.ecs.lab.impact.propagation;

import edu.university.ecs.lab.impact.propagation.dependencies.EntityDepAssessor;
import edu.university.ecs.lab.common.models.MsSystem;
import edu.university.ecs.lab.delta.models.SystemChange;

import java.io.IOException;

import static edu.university.ecs.lab.common.utils.IRParserUtils.*;

public class PropagationManager {

    private final MsSystem msSystem;
    private final SystemChange systemChange;

    public PropagationManager(String irPath, String deltaPath) throws IOException {
        msSystem = parseIRSystem(irPath);
        systemChange = parseSystemChange(deltaPath);
    }

    public void identify() {
        EntityDepAssessor.run(systemChange, msSystem);
    }
}
