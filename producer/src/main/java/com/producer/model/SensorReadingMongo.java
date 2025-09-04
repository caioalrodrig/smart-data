package com.producer.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "sensors")
public class SensorReadingMongo {

  @Id
  private String id;

  private String name;
  private String type;

  private List<Reading> readings;

  @Data
  public static class Reading {
    private Double value;
    private Date timestamp;
  }
}