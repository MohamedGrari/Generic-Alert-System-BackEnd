package com.jobSchedule.JobScheduler.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class EmployerController {
    @Autowired
    EmployerService employerService;
    @GetMapping("/employers")
    public List<Employer> getEmployers(){
        return employerService.findAllEmployer();
    }
    @GetMapping("/employer/{id}")
    public Optional<Employer> getEmployerById(@PathVariable("id") Long id){
        return employerService.findEmployerById(id);
    }
    @PostMapping("/saveEmployer")
    public Employer saveEmployer (@Valid @RequestBody Employer employer){
        return employerService.saveEmployer(employer);
    }
    @DeleteMapping("/delete")
    public void deleteEmployer(@Valid @RequestBody Employer employer){
        employerService.deleteEmployer(employer);
    }
}
