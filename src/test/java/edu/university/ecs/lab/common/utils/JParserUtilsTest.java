package edu.university.ecs.lab.common.utils;

import edu.university.ecs.lab.common.models.JController;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class JParserUtilsTest {

    @Test
    void parseController1() {
        String[] pathFragments = {"test-repos", "train-ticket", "ts-order-service", "src", "main", "java", "order", "controller", "OrderController.java"};
        StringBuilder path = new StringBuilder();
        for (int i = 0; i < pathFragments.length -1 ; i++) {
            path.append(pathFragments[i]).append(File.separator);
        }

        JController controller = null;

        try {
            File controllerFile = new File(path.toString());
            controller = JParserUtils.parseController(controllerFile);
        }
        catch (Exception e) {
            fail();
        }

        assert controller != null;

        assertEquals(path.toString(), controller.getClassPath());

        assert controller.getPackageName().equals("order.controller");

        assert controller.getClassName().equals("OrderController");

        assert controller.getMethods() != null;
        //todo

        assert controller.getFields() != null;
        //todo

        assert controller.getMethodCalls() != null;
        //todo

        assert controller.getEndpoints() != null;
        assert controller.getEndpoints().size() == 16;
        //todo



    }

    @Test
    void parseService() {
    }

    @Test
    void parseClass() {
    }

    @Test
    void parseMethods() {
    }

    @Test
    void parseEndpoints() {
    }

    @Test
    void parseMethod() {
    }

    @Test
    void parseRestCalls() {
    }

    @Test
    void parseMethodCalls() {
    }
}