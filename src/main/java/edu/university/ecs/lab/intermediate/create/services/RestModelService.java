package edu.university.ecs.lab.intermediate.create.services;

import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.utils.ParserUtils;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.MsModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Service for extracting REST endpoints and dependencies for a given microservice. */
public class RestModelService {
  /**
   * Recursively scan the files in the given repository path and extract the endpoints and
   * dependencies for a single microservice.
   *
   * @param rootPath root path to cloned repository folder
   * @param pathToMs the path to the microservice TLD
   * @return model of a single service containing the extracted endpoints and dependencies
   */
  public static MsModel recursivelyScanFiles(String rootPath, String pathToMs) {
    String repoPath = rootPath + pathToMs;
    System.out.println("Scanning repository '" + repoPath + "'...");
    MsModel model = new MsModel();

    List<JController> controllers = new ArrayList<>();
    List<JService> services = new ArrayList<>();
    List<JClass> dtos = new ArrayList<>();
    List<JClass> repositories = new ArrayList<>();
    List<JClass> entities = new ArrayList<>();

    File localDir = new File(repoPath);
    if (!localDir.exists() || !localDir.isDirectory()) {
      System.err.println("Invalid path given: " + repoPath);
      return null;
    }

    scanDirectory(localDir, controllers, services, dtos, repositories, entities);

    model.setControllers(controllers);
    model.setServices(services);
    model.setDtos(dtos);
    model.setRepositories(repositories);
    model.setEntities(entities);

    System.out.println("Done!");
    return model;
  }

  /**
   * Recursively scan the given directory for files and extract the endpoints and dependencies.
   *
   * @param directory the directory to scan
   */
  public static void scanDirectory(
      File directory,
      List<JController> controllers,
      List<JService> services,
      List<JClass> dtos,
      List<JClass> repositories,
      List<JClass> entities) {
    File[] files = directory.listFiles();

    if (files != null) {
      for (File file : files) {
        if (file.isDirectory()) {
          scanDirectory(file, controllers, services, dtos, repositories, entities);
        } else if (file.getName().endsWith(".java")) {
          scanFile(file, controllers, services, dtos, repositories, entities);
        }
      }
    }
  }

  /**
   * Scan the given file for endpoints and calls to other services.
   *
   * @param file the file to scan
   */
  public static void scanFile(
      File file,
      List<JController> controllers,
      List<JService> services,
      List<JClass> dtos,
      List<JClass> repositories,
      List<JClass> entities) {
    try {
      if (file.getName().contains("Controller")) {
        JController controller = ParserUtils.parseController(file);
        if (Objects.nonNull(controller)) {
          controllers.add(controller);
        }
      } else if (file.getName().contains("Service")) {
        JService jService = ParserUtils.parseService(file);
        if (Objects.nonNull(jService)) {
          services.add(jService);
        }
      } else if (file.getName().toLowerCase().contains("dto")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          dtos.add(jClass);
        }
      } else if (file.getName().contains("Repository")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          repositories.add(jClass);
        }
      } else if (file.getParent().toLowerCase().contains("entity")
          || file.getParent().toLowerCase().contains("model")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          entities.add(jClass);
        }
      }

      // todo: configs? utils? (everything else? -_-)
    } catch (IOException e) {
      System.err.println("Could not parse file: " + e.getMessage());
    }
  }

  public static JClass scanFile(File file) {
    try {
      if (file.getName().contains("Controller")) {
        JController controller = ParserUtils.parseController(file);
        if (Objects.nonNull(controller)) {
          return controller;
        }
      } else if (file.getName().contains("Service")) {
        JService jService = ParserUtils.parseService(file);
        if (Objects.nonNull(jService)) {
          return jService;
        }
      } else if (file.getName().toLowerCase().contains("dto")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          return jClass;
        }
      } else if (file.getName().contains("Repository")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          return jClass;
        }
      } else if (file.getParent().toLowerCase().contains("entity")
              || file.getParent().toLowerCase().contains("model")) {
        JClass jClass = ParserUtils.parseClass(file);
        if (Objects.nonNull(jClass)) {
          return jClass;
        }
      }

      // todo: configs? utils? (everything else? -_-)
    } catch (IOException e) {
      System.err.println("Could not parse file: " + e.getMessage());
    }
    return null;
  }
}
