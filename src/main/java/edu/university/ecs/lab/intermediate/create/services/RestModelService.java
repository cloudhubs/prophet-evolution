package edu.university.ecs.lab.intermediate.create.services;

import edu.university.ecs.lab.common.models.JController;
import edu.university.ecs.lab.common.models.JService;
import edu.university.ecs.lab.common.utils.JsonToObjectUtils;
import edu.university.ecs.lab.common.models.JClass;
import edu.university.ecs.lab.common.models.Microservice;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Service for extracting REST endpoints and dependencies for a given microservice. */
public class RestModelService {
  /**
   * Recursively scan the files in the given repository path and extract the endpoints and
   * dependencies for a single microservice.
   *
   * @param rootPath clonePath specified by {@link edu.university.ecs.lab.common.config.models.InputConfig}
   * @param pathToMs the path to the microservice directory NOT including the clonePath ("\sysName\...\msName")
   * @return model of a single service containing the extracted endpoints and dependencies
   */
  public static Microservice recursivelyScanFiles(String rootPath, String pathToMs) {
    String repoPath = rootPath + pathToMs;
    System.out.println("Scanning repository '" + repoPath + "'...");
    Microservice model = new Microservice();

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

    // Set the id of the microservice to the last part of the msPath
    model.setId(repoPath.substring(repoPath.lastIndexOf(File.separator) + 1));

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
   * @apiNote CURRENT LIMITATION: We detect controllers/services/dtos/repositories/entities based on literally
   * having that string within the file name. This is a naive approach and should be improved.
   */
  public static void scanFile(
      File file,
      List<JController> controllers,
      List<JService> services,
      List<JClass> dtos,
      List<JClass> repositories,
      List<JClass> entities) {
    try {
      JClass jClass = JsonToObjectUtils.parseClass(file);

      if (jClass == null) {
        return;
      }

      // Switch through class roles and handle additional logic if needed
      switch (jClass.getClassRole()) {
          case CONTROLLER:
            controllers.add((JController) jClass);
            break;
          case SERVICE:
            services.add((JService) jClass);
            break;
          case DTO:
            dtos.add(jClass);
            break;
          case REPOSITORY:
            repositories.add(jClass);
            break;
          case ENTITY:
            entities.add(jClass);
            break;
          default:
            break;
      }
    } catch (IOException e) {
      System.err.println("Could not parse file due to unrecognized type: " + e.getMessage());
    }
  }

}
