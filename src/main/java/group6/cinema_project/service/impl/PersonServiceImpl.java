package group6.cinema_project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import group6.cinema_project.entity.Person;
import group6.cinema_project.repository.PersonRepository;
import group6.cinema_project.service.PersonService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService {
    private final PersonRepository personRepository;

    @Override
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    public Person findById(Long id) {
        return personRepository.findById(id);
    }

    @Override
    public Person saveOrUpdate(Person person) {
        return personRepository.save(person);
    }

    @Override
    public void delete(Person person) {
        personRepository.delete(person);
    }
}
