package edu.university.ecs.lab.impact.propagation;

import edu.university.ecs.lab.impact.propagation.dependencies.EntityDepAssessor;
import edu.university.ecs.lab.intermediate.merge.models.MsSystem;
import edu.university.ecs.lab.intermediate.merge.models.SystemChange;

import java.io.IOException;

import static edu.university.ecs.lab.common.utils.IRParserUtils.parseDelta;
import static edu.university.ecs.lab.common.utils.IRParserUtils.parseIRSystem;

public class PropagationManager {

    private final MsSystem msSystem;
    private final SystemChange systemChange;

    public PropagationManager(String irPath, String deltaPath) throws IOException {
        msSystem = parseIRSystem(irPath);
        systemChange = parseDelta(deltaPath);
    }

    public void identify() {
        EntityDepAssessor.run(systemChange, msSystem);
    }
}
