package com.citizenweb.training.reactiveapp;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class ReactiveAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReactiveAppApplication.class, args);
    }

    /*@Bean
    CommandLineRunner init(PersonRepository personRepository) {
        return args -> {
            Flux<Person> personFlux = Flux
                    .just(Person.builder().name("FREDO").age(50).build())
                    .flatMap(personRepository::save);

            personFlux
                    .thenMany(personRepository.findAll())
                    .subscribe(log::info);
        };
    }*/

}
