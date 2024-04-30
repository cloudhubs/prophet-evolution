package edu.university.ecs.lab.common.utils;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import edu.university.ecs.lab.common.models.*;
import edu.university.ecs.lab.common.models.enums.ClassRole;
import edu.university.ecs.lab.common.models.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JParserUtilsTest {

    public static final String SEP = File.separator;
    private final String controllerPath1 = "test-repos" + SEP + "train-ticket" + SEP + "ts-order-service" + SEP + "src" + SEP + "main" + SEP + "java" + SEP + "order" + SEP + "controller" + SEP + "OrderController.java";
    private final String servicePath1 = "test-repos" + SEP + "train-ticket" + SEP + "ts-order-service" + SEP + "src" + SEP + "main" + SEP + "java" + SEP + "order" + SEP + "service" + SEP + "OrderServiceImpl.java";

//    @Test
//    void parseController1() {
//        JController controller = null;
//
//        try {
//            File controllerFile = new File(controllerPath1);
//            controller = SourceToObjectUtils.parseController(controllerFile);
//        }
//        catch (Exception e) {
//            fail();
//        }
//
//        assert controller != null;
//
//        assertEquals("./src/main/java/order/controller/OrderController.java", controller.getClassPath());
//
//        assert controller.getPackageName().equals("order.controller");
//
//        assert controller.getClassName().equals("OrderController");
//
//        assert controller.getMethods() != null;
//        assert controller.getMethods().size() == 16;
//
//        assert controller.getFields() != null;
//        assert controller.getFields().size() == 2;
//        assert controller.getFields().contains(new Field("OrderService", "orderService"));
//        assert controller.getFields().contains(new Field("Logger", "LOGGER"));
//
//        assert controller.getMethodCalls() != null;
//        // Random 5
////        assert controller.getMethodCalls().contains(new MethodCall("getLogger", "LoggerFactory", null));
//
//        assert controller.getEndpoints() != null;
//        assert controller.getEndpoints().size() == 16;
//        assert controller.getEndpoints().contains(new Endpoint("/api/v1/orderservice/welcome", "GetMapping", "GET", null, null));
//
//
//
//    }
//
//    @Test
//    void parseService1() {
//
//    }
//
//    @Test
//    void parseClassC1() {
//        JClass jClass = null;
//
//        try {
//            jClass = JParserUtils.parseClass(new File(controllerPath1));
//        } catch (IOException e) {
//            fail();
//        }
//
//        assert jClass != null;
//        assertEquals("OrderController", jClass.getClassName());
//        assertEquals("./src/main/java/order/controller/OrderController.java", jClass.getClassPath());
//        assertEquals("order.controller", jClass.getPackageName());
////        assertEquals(ClassRole.CONTROLLER, jClass.getClassRole());
//
//        assert jClass.getFields().size() == 2;
//        assert jClass.getFields().contains(new Field("OrderService", "orderService"));
//        assert jClass.getFields().contains(new Field("Logger", "LOGGER"));
//    }
//
//    @Test
//    void parseClassS1() {
//        JClass jClass = null;
//
//        try {
//            jClass = JParserUtils.parseClass(new File(servicePath1));
//        } catch (IOException e) {
//            fail();
//        }
//
//        assert jClass != null;
//        assertEquals("OrderServiceImpl", jClass.getClassName());
//        assertEquals("./src/main/java/order/service/OrderServiceImpl.java", jClass.getClassPath());
//        assertEquals("order.service", jClass.getPackageName());
////        assertEquals(ClassRole.SERVICE, jClass.getClassRole());
//
//        assert jClass.getFields().size() == 6;
//        assert jClass.getFields().contains(new Field("OrderRepository", "orderRepository"));
//        assert jClass.getFields().contains(new Field("RestTemplate", "restTemplate"));
//        assert jClass.getFields().contains(new Field("Logger", "LOGGER"));
//        assert jClass.getFields().contains(new Field("DiscoveryClient", "discoveryClient"));
//        assert jClass.getFields().contains(new Field("String", "success"));
//        assert jClass.getFields().contains(new Field("String", "orderNotFound"));
//    }
//
//    @Test
//    void parseMethodsC1() {
//        CompilationUnit cu = null;
//        try {
//            cu = StaticJavaParser.parse(new File(controllerPath1));
//        } catch (FileNotFoundException e) {
//            fail();
//        }
//
//        List<Method> methods = JParserUtils.parseMethods(cu);
//
//        assert methods.size() == 16;
//        assert methods.contains(new Method("home", "public","",  "String"));
//        assert methods.contains(new Method("getTicketListByDateAndTripId", "public","@RequestBody Seat seatRequest, @RequestHeader HttpHeaders headers",  "HttpEntity"));
//        assert methods.contains(new Method("createNewOrder", "public","@RequestBody Order createOrder, @RequestHeader HttpHeaders headers",  "HttpEntity"));
//        assert methods.contains(new Method("addCreateNewOrder", "public","@RequestBody Order order, @RequestHeader HttpHeaders headers",  "HttpEntity"));
//        // ...
//        assert methods.contains(new Method("findAllOrder", "public","@RequestHeader HttpHeaders headers",  "HttpEntity"));
//
//
//    }
//
//    @Test
//    void parseMethodsS1() {
//        CompilationUnit cu = null;
//        try {
//            cu = StaticJavaParser.parse(new File(servicePath1));
//        } catch (FileNotFoundException e) {
//            fail();
//        }
//
//        List<Method> methods = JParserUtils.parseMethods(cu);
//
//        assert methods.size() == 21;
//        assert methods.contains(new Method("getServiceUrl", "private", "String serviceName", "String"));
//        assert methods.contains(new Method("getSoldTickets", "public", "Seat seatRequest, HttpHeaders headers", "Response"));
//        // ...
//        assert methods.contains(new Method("updateOrder", "public", "Order order, HttpHeaders headers", "Response"));
//
//    }
//
//    @Test
//    void parseEndpointsC1() {
//
//        List<Endpoint> endpoints = null;
//        try {
//            endpoints = JParserUtils.parseEndpoints(new File(controllerPath1));
//        } catch (IOException e) {
//            fail();
//        }
//
//
//        assert endpoints.size() == 16;
//        assert endpoints.contains(new Endpoint("/api/v1/orderservice/welcome", "GetMapping", "GET", null, null));
//        //todo add more
//
//
//    }
//
//    @Test
//    void parseMethodCallsC1() {
//
//        List<MethodCall> methodCalls = null;
//
//        try {
//            methodCalls = JParserUtils.parseMethodCalls(new File(controllerPath1));
//        } catch (IOException e) {
//            fail();
//        }
//
//        // Random 5
////        assert methodCalls.contains(new MethodCall("getLogger", "LoggerFactory", null));
//        //todo how does this work?
//
//    }
//
//    @Test
//    void parseMethodCallsS1() {
//
//        List<MethodCall> methodCalls = null;
//
//        try {
//            methodCalls = JParserUtils.parseMethodCalls(new File(servicePath1));
//        } catch (IOException e) {
//            fail();
//        }
//
//        // Random 5
//        //todo how does this work?
//    }
//
//
//    @Test
//    void parseRestCalls() {
//
//        List<RestCall> restCalls = null;
//        try {
//            restCalls = JParserUtils.parseRestCalls(new File(servicePath1));
//        }
//        catch (Exception e) {
//            fail();
//        }
//
//        assertEquals(1, restCalls.size());
//        RestCall r = new RestCall("exchange", HttpMethod.POST, 3);
//        r.setSourceFile("./src/main/java/order/service/OrderServiceImpl.java");
//        r.setApi("/api/v1/stationservice/stations/namelist");
//        r.setCalledFieldName("restTemplate");
//        r.setParentMethod("queryForStationId");
//        assertTrue(restCalls.contains(r));
//
//
//
//
//
//    }

}