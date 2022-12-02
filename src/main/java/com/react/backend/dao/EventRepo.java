package com.react.backend.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.react.backend.model.Event;

@Repository
public interface EventRepo extends MongoRepository<Event, Integer>{}