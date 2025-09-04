package com.producer.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.producer.model.SensorReadingMongo;

public interface SensorReadingMongoRepository extends MongoRepository<SensorReadingMongo, String> {

  SensorReadingMongo findByName(String name);

  @Query("{ '_id' : ?0 }") 
  @Update("{ '$push' : { 'readings' : { '$each' : ?1 } } }")
  void pushReadings(String id, List<SensorReadingMongo.Reading> readings);
}
