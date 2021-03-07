package com.zubayear.jpa.assemblers;

import com.zubayear.jpa.controllers.PersonController;
import com.zubayear.jpa.entity.Person;
import com.zubayear.jpa.entity.PersonType;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class PersonAssembler implements RepresentationModelAssembler<Person, EntityModel<Person>> {
    @Override
    public EntityModel<Person> toModel(Person person) {
        EntityModel<Person> personEntityModel = EntityModel.of(person,
                linkTo(methodOn(PersonController.class).getPerson(person.getId())).withSelfRel(),
                linkTo(methodOn(PersonController.class).allPersons()).withRel("all-persons"));
        if (person.getPersonType() == PersonType.DEVELOPER) {
            personEntityModel.add(linkTo(methodOn(PersonController.class).engineer(person.getId())).withRel("engineer"));
        }
        return personEntityModel;
    }
}
