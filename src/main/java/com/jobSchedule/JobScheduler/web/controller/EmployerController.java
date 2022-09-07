package com.jobSchedule.JobScheduler.web.controller;

import com.jobSchedule.JobScheduler.web.model.Employer;
import com.jobSchedule.JobScheduler.web.service.EmployerService;
import com.jobSchedule.JobScheduler.web.dto.EmployerDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class EmployerController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    EmployerService employerService;
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/employers")
    public List<EmployerDto> getEmployers(){
        //return employerService.findAllEmployer();
        return employerService.findAllEmployer().stream().map(post -> modelMapper.map(post, EmployerDto.class))
                .collect(Collectors.toList());
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/employer/{id}")
    public Optional<EmployerDto> getEmployerById(@PathVariable("id") Long id){
        Optional<Employer> employer = employerService.findEmployerById(id);
        return modelMapper.map(employer, (Type) EmployerDto.class);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("/saveEmployer")
    public EmployerDto saveEmployer (@Valid @RequestBody EmployerDto employerDto){
        //return employerService.saveEmployer(employer);
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        Employer employer = employerService.saveEmployer(employerRequest);
        return modelMapper.map(employer, EmployerDto.class);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @PutMapping("/updateEmployer")
    public EmployerDto updateEmployer(@RequestBody EmployerDto employerDto) throws Exception {
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        Employer employerResponse = employerService.updateEmployer(employerRequest);
        return modelMapper.map(employerResponse, EmployerDto.class);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/deleteEmployer")
    public void deleteEmployer(@Valid @RequestBody EmployerDto employerDto){
        //employerService.deleteEmployer(employer);
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        employerService.deleteEmployer(employerRequest);
    }
}
