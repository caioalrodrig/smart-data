package com.producer.repository;

import com.producer.model.Reading;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

  @Query("SELECT r FROM Reading r WHERE r.sensor.id = :sensorId")
  List<Reading> findAllBySensorId(Long sensorId);
}