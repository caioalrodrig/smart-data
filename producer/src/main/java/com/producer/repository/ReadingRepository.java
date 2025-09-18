package com.producer.repository;

import com.producer.model.Reading;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReadingRepository extends JpaRepository<Reading, Long> {

  @Query("SELECT r FROM Reading r WHERE r.sensor.id = :sensorId")
  List<Reading> findAllBySensorId(Long sensorId);

  @Query("""
      SELECT r FROM Reading r
      WHERE r.sensor.id = :sensorId
        AND r.timestamp >= :startDate
        AND r.timestamp <= :endDate
      ORDER BY r.timestamp
      """)
  List<Reading> findAllBySensorIdAndTimestampBetween(
      Long sensorId,
      LocalDateTime startDate,
      LocalDateTime endDate);

  @Query("""
      SELECT r FROM Reading r
        WHERE r.timestamp >= :startDate
        ORDER BY r.timestamp
      """)
  List<Reading> findAllByTimestampBetween(LocalDateTime startDate, LocalDateTime endDate);
}