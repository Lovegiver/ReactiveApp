package com.citizenweb.training.reactiveapp.repository;

import com.citizenweb.training.reactiveapp.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface PersonRepository extends ReactiveMongoRepository<Person, String> {
}
