package edu.university.ecs.lab.common.models;

import javax.json.JsonObject;

/**
 * Interface for classes that can be serialized to JSON object
 */
public interface JsonSerializable {
  JsonObject toJsonObject();
}
