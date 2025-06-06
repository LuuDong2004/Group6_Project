package group6.cinema_project.service;

import java.util.List;

import group6.cinema_project.entity.Person;

public interface PersonService {
    List<Person> findAll();

    Person findById(Long id);

    Person saveOrUpdate(Person person);
    
    void delete(Person person);
}
