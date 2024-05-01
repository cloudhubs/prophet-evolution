package edu.university.ecs.lab.common.utils;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SourceToObjectUtilsTest {

  public static final String SEP = File.separator;
  private final String controllerPath1 =
      "test-repos"
          + SEP
          + "train-ticket"
          + SEP
          + "ts-order-service"
          + SEP
          + "src"
          + SEP
          + "main"
          + SEP
          + "java"
          + SEP
          + "order"
          + SEP
          + "controller"
          + SEP
          + "OrderController.java";
  private final String servicePath1 =
      "test-repos"
          + SEP
          + "train-ticket"
          + SEP
          + "ts-order-service"
          + SEP
          + "src"
          + SEP
          + "main"
          + SEP
          + "java"
          + SEP
          + "order"
          + SEP
          + "service"
          + SEP
          + "OrderServiceImpl.java";

  @Test
  void parseClassC1() {
    //        JClass jClass = null;
    //
    //        try {
    //            jClass = SourceToObjectUtils.parseClass(new File(controllerPath1));
    //        } catch (IOException e) {
    //            fail();
    //        }
    //
    //        assert jClass != null;
    //        assertEquals("OrderController", jClass.getClassName());
    //        assertEquals("./src/main/java/order/controller/OrderController.java",
    // jClass.getClassPath());
    //        assertEquals("order.controller", jClass.getPackageName());
    ////        assertEquals(ClassRole.CONTROLLER, jClass.getClassRole());
    //
    //        assert jClass.getFields().size() == 2;
    //        assert jClass.getFields().contains(new Field("OrderService", "orderService"));
    //        assert jClass.getFields().contains(new Field("Logger", "LOGGER"));
  }
}
