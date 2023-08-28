package com.dependencyProject.dependencyproject.rest;

import com.dependencyProject.dependencyproject.model.*;
import com.dependencyProject.dependencyproject.mapping.DeveloperResponse;
import com.dependencyProject.dependencyproject.tax.*;
import com.dependencyProject.dependencyproject.validation.DeveloperValidation;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developer")
public class DeveloperController {
    private Map<Integer, Developer> developers;
    private Taxable taxable;

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }

    @Autowired
    public DeveloperController(@Qualifier("developerTax") Taxable taxable) {
        this.taxable = taxable;
    }

    @GetMapping("/")
    public List<Developer> get() {
        return developers.values().stream().toList();
    }

    @GetMapping("/{id}")
    public DeveloperResponse getById(@PathVariable int id) {
        if (!DeveloperValidation.isIdValid(id)) {
            return new DeveloperResponse(null, "Id id not valid", 400);
        }
        if (developers.containsKey(id)) {
            return new DeveloperResponse(null, "Developer with given is not exist: " + id, 404);
        }
        return new DeveloperResponse(developers.get(id), "Success", 200);
    }

    @PostMapping("/")
    public DeveloperResponse save(@RequestBody Developer developer) {
        Developer savedDeveloper = DeveloperFactory.createDeveloper(developer, taxable);
        if (savedDeveloper == null) {
            return new DeveloperResponse(null, "Developer with given is not valid", 400);
        }
        if (developers.containsKey(developer.getId())) {
            return new DeveloperResponse(null, "Developer with given id is already exist: " + developer.getId(), 400);
        }
        developers.put(developer.getId(), savedDeveloper);
        return new DeveloperResponse(developers.get(developer.getId()), "Success", 201);
    }

    @PutMapping("/{id}")
    public DeveloperResponse update(@PathVariable int id, @RequestBody Developer developer) {
        if (!developers.containsKey(id)) {
            return new DeveloperResponse(null, "Developer with given id is not exist: " + id, 400);
        }
        developer.setId(id);
        Developer updateDeveloper = DeveloperFactory.createDeveloper(developer, taxable);

        if (updateDeveloper == null) {
            return new DeveloperResponse(null, "Developer with given experience is not valid", 400);
        }
        if (!DeveloperValidation.isDeveloperValid(developer)) {
            return new DeveloperResponse(null, "Developer credentials are not valid", 400);
        }
        developers.put(id, updateDeveloper);
        return new DeveloperResponse(developers.get(id), "Success", 200);
    }

    @DeleteMapping("/{id}")
    public DeveloperResponse delete(@PathVariable int id) {
        if (!developers.containsKey(id)) {
            return new DeveloperResponse(null, "Developer with given id is not exist: " + id, 400);
        }
        Developer developer = developers.get(id);
        developers.remove(id);
        return new DeveloperResponse(developer, "Sucess", 200);
    }
}



