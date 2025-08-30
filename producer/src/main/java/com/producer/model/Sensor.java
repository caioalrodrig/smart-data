package com.producer.model;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import lombok.Data;

import jakarta.validation.constraints.Pattern;

@Data
@Entity
public class Sensor {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "farm_id", nullable = false)
  private Farm farm;

  @Column(length = 10, nullable=false)
  @Pattern(regexp = "Ativo|Inativo")
  private String status= "Ativo";

  @Column(length = 20, nullable=false)
  private String name;
}