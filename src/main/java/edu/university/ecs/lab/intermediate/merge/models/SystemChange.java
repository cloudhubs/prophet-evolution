package edu.university.ecs.lab.intermediate.merge.models;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SystemChange {
  @SerializedName("controllers")
  private List<Delta> controllers;

  @SerializedName("services")
  private List<Delta> services;

  @SerializedName("dtos")
  private List<Delta> dtos;

  @SerializedName("repositories")
  private List<Delta> repositories;

  @SerializedName("entities")
  private List<Delta> entities;
}
