package com.zubayear.jpa.controllers;

import com.zubayear.jpa.assemblers.PersonAssembler;
import com.zubayear.jpa.entity.Person;
import com.zubayear.jpa.entity.PersonType;
import com.zubayear.jpa.exceptions.PersonNotFoundException;
import com.zubayear.jpa.repository.PersonRepository;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.mediatype.problem.Problem;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class PersonController {

    private final PersonRepository personRepository;
    private final PersonAssembler personAssembler;

    public PersonController(PersonRepository personRepository, PersonAssembler personAssembler) {
        this.personRepository = personRepository;
        this.personAssembler = personAssembler;
    }

    @GetMapping("/persons")
    public CollectionModel<EntityModel<Person>> allPersons() {
        List<EntityModel<Person>> persons = personRepository.findAll()
                .stream().map(personAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(persons, linkTo(methodOn(PersonController.class).allPersons()).withSelfRel());
    }

    @GetMapping("/persons/{id}")
    public EntityModel<Person> getPerson(@PathVariable Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        return personAssembler.toModel(person);
    }

    @PostMapping("/persons")
    public ResponseEntity<?> addPerson(@RequestBody Person person) {
        EntityModel<Person> personEntityModel = personAssembler.toModel(personRepository.save(person));
        return ResponseEntity
                .created(personEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(personEntityModel);
    }


    @PutMapping("/persons/{id}")
    public EntityModel<Person> editPerson(@RequestBody Person newPerson, @PathVariable Long id) {
        Person updatedPerson = personRepository.findById(id)
                .map(person -> {
                    person.setName(newPerson.getName());
                    person.setPersonType(newPerson.getPersonType());
                    return person;
                })
                .orElseGet(() -> {
                    newPerson.setId(id);
                    return personRepository.save(newPerson);
                });
        return personAssembler.toModel(updatedPerson);
    }

    @DeleteMapping("/persons/{id}")
    public ResponseEntity<?> deletePerson(@PathVariable Long id) {
        personRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/persons/{id}/engineer")
    public ResponseEntity<?> engineer(@PathVariable Long id) {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(id));
        if (person.getPersonType() == PersonType.DEVELOPER) {
            return ResponseEntity.ok(personAssembler.toModel(personRepository.save(person)));
        }
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .header(HttpHeaders.CONTENT_TYPE, MediaTypes.HTTP_PROBLEM_DETAILS_JSON_VALUE)
                .body(Problem.create()
                .withTitle("Method not allowed")
                .withDetail(String.format("You can't change an %s", person.getPersonType())));
    }

    @GetMapping("/persons/{id}/tester")
    public ResponseEntity<?> tester(@PathVariable Long id) {
        return ResponseEntity.ok().build();
    }
}
