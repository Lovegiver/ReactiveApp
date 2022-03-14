package com.citizenweb.training.reactiveapp;

import com.citizenweb.training.reactiveapp.model.Person;
import com.citizenweb.training.reactiveapp.repository.PersonRepository;
import com.citizenweb.training.reactiveapp.service.PersonService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@Log4j2
public class Launcher implements CommandLineRunner {

    private final PersonService personService;
    private final PersonRepository personRepository;

    public Launcher(PersonService personService, PersonRepository personRepository) {
        this.personService = personService;
        this.personRepository = personRepository;
    }

    @Override
    public void run(String... args) throws Exception {

        long timeAtStart = System.currentTimeMillis();
        log.info("Launching [ ReactiveApp ]");

        int size = 10;
        char charToFind = 'O';

        Scheduler scheduler = Schedulers.newBoundedElastic(5, 10, "TalanThread");

        List<String> personNameList = new ArrayList<>(size);
        List<Integer> personAgeList = new ArrayList<>(size);
        List<Person> savedPersonList = new ArrayList<>(size);

        Stream<String> nameStream = Stream
                .generate(personService::buildName);
        Stream<Integer> ageStream = Stream
                .generate(personService::computeAge);

        ConnectableFlux<Person> personFlux = Flux.zip(Flux.fromStream(nameStream), Flux.fromStream(ageStream),
                (name,age) -> Person.builder().name(name).age(age).build())
                .take(size)
                .replay();

        ConnectableFlux<Person> personFluxFromDatabase = personRepository.findAll().publish();

        ConnectableFlux<Person> personToLog = Flux.from(personFlux).publish();
        ConnectableFlux<Person> personToPushInCollection = Flux.from(personFlux).publish();

        personFlux
                //.log()
                .subscribeOn(scheduler)
                .subscribe(p -> {
                    Person person = recordPerson(p).block();
                    log.info("[ {} ] saved", person);
                });

        personFluxFromDatabase
                //.log()
                .subscribeOn(scheduler)
                .subscribe(p -> {
                    log.info("Getting objects from DB : {}", p);
                    if (p.getName().contains(Character.toString(charToFind).toUpperCase())) {
                        log.info("[ {} ] has an '{}' !!", p, charToFind);
                    }
                    personNameList.add(p.getName());
                    personAgeList.add(p.getAge());
                });

        personToLog.log()
                .subscribeOn(scheduler).subscribe(log::info);
        personToPushInCollection.log()
                .subscribeOn(scheduler).subscribe(savedPersonList::add);


        personFlux.connect();
        personFluxFromDatabase.connect();
        personToLog.connect();
        personToPushInCollection.connect();

        while (!(personAgeList.size() == size && personNameList.size() == size)) {
            log.info("Waiting...");
            Thread.sleep(1000);
        }

        double averageCharToFind = personService.computeMean(personNameList,charToFind);
        log.info("Average number of [ '{}' ] in names : [ {} ]", charToFind, averageCharToFind);
        var meanAge = personAgeList.stream().mapToInt(a -> a).average().orElse(0);
        log.info("Average age is [ {} ]", meanAge);

        log.info("[ {} ] Person objects were saved", savedPersonList.size());

        log.info("Elapsed time since start = [ {} ] ms", System.currentTimeMillis() - timeAtStart);
        System.exit(0);

    }

    private Mono<Person> recordPerson(Person person) {
        Mono<Person> savedPersonMono = personRepository.save(person);
        log.info("Saving [ {} ]", person.getName());
        return savedPersonMono;
    }


}
