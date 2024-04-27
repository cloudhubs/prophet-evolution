package edu.university.ecs.lab.common.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * Represents the overarching structure of a microservice system. It is composed of classes which
 * hold all information in that class.
 */
@Getter
public class Microservice {
  /** The name of the service (ex: "ts-assurance-service") */
  @SerializedName("id")
  private String id;

  /** The commit id of the service as cloned */
  @SerializedName("commitId")
  private String commit;

  /** List of classes */
  private List<JController> controllers;
  private List<JService> services;
  private List<JClass> dtos;
  private List<JClass> repositories;
  private List<JClass> entities;

  public void setId(String id) {
    Objects.requireNonNull(id, "id cannot be null");
    this.id = id.replaceAll("\\\\", "/");
  }

  public void setCommit(String commit) {
    Objects.requireNonNull(commit, "commit cannot be null");
    this.commit = commit;
  }

  public void setControllers(List<JController> controllers) {
    Objects.requireNonNull(controllers, "controllers cannot be null");
    this.controllers = controllers;
    propagateId(this.controllers);
  }

  public void setServices(List<JService> services) {
    Objects.requireNonNull(services, "services cannot be null");
    this.services = services;
    propagateId(this.services);
  }

  public void setDtos(List<JClass> dtos) {
    Objects.requireNonNull(dtos, "dtos cannot be null");
    this.dtos = dtos;
    propagateId(this.dtos);
  }

  public void setRepositories(List<JClass> repositories) {
    Objects.requireNonNull(repositories, "repositories cannot be null");
    this.repositories = repositories;
    propagateId(this.repositories);
  }

  public void setEntities(List<JClass> entities) {
    Objects.requireNonNull(entities, "entities cannot be null");
    this.entities = entities;
    propagateId(this.entities);
  }

  private <T extends JClass> void propagateId(List<T> classes) {
    Objects.requireNonNull(this.id);
    Objects.requireNonNull(classes);
    classes.forEach(c -> c.setMsId(id));
  }

  public Microservice(String id,
                      String commit,
                      List<JController> controllers,
                      List<JService> services,
                      List<JClass> dtos,
                      List<JClass> repositories,
                      List<JClass> entities) {
    this.setCommit(commit);
    this.setId(id);
    this.setControllers(controllers);
    this.setServices(services);
    this.setDtos(dtos);
    this.setRepositories(repositories);
    this.setEntities(entities);
  }

  /** Default constructor, init lists as empty */
  public Microservice() {}
}
