package edu.university.ecs.lab.common.models;

import javax.json.JsonObject;

public interface JsonSerializable {
  JsonObject toJsonObject();
}
