package com.producer.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.producer.model.SensorReadingMongo;

public interface SensorReadingMongoRepository extends MongoRepository<SensorReadingMongo, String> {

  SensorReadingMongo findByName(String name);

  @Query("{ '_id' : ?0 }")
  @Update("{ '$push' : { 'readings' : { '$each' : ?1 } } }")
  void pushReadings(String id, List<SensorReadingMongo.Reading> readings);

  @Query("{ 'readings.timestamp': { $gte: ?0 } }")
  List<SensorReadingMongo> findAllByTimestampBetween(Date startDate);

  @Aggregation(pipeline = {
      "{ $match: { 'readings.timestamp': { $gte: ?0, $lte: ?1 } } }",
      "{ $unwind: '$readings' }",
      "{ $match: { 'readings.timestamp': { $gte: ?0, $lte: ?1 } } }",
      "{ $sort: { 'readings.timestamp': 1 } }"
  })
  List<SensorReadingMongo> findReadingsBetween(Date startDate, Date endDate);
}
