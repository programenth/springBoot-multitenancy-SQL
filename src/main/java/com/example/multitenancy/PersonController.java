package com.example.multitenancy;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/people")
public class PersonController {
    private final PersonRepository repo;
    public PersonController(PersonRepository repo) { this.repo = repo; }

    @GetMapping
    public List<Person> getAll() { return repo.findAll(); }

    @PostMapping
    public Person add(@RequestBody Person p) { return repo.save(p); }
}

