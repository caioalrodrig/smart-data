package com.producer.model;

import java.sql.Timestamp;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import lombok.Data;


@Data
@Entity
public class Reading {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "sensor_id", nullable = false)
  private Sensor sensor;

  @Column(name = "reading_value", nullable = false)
  private Double value;

  @Column(name = "reading_timestamp", nullable = false)
  private Timestamp timestamp;
}