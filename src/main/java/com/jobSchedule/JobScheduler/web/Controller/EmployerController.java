package com.jobSchedule.JobScheduler.web.Controller;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Service.EmployerService;
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
    @GetMapping("/employers")
    public List<EmployerDto> getEmployers(){
        //return employerService.findAllEmployer();
        return employerService.findAllEmployer().stream().map(post -> modelMapper.map(post, EmployerDto.class))
                .collect(Collectors.toList());
    }
    @GetMapping("/employer/{id}")
    public Optional<EmployerDto> getEmployerById(@PathVariable("id") Long id){
        Optional<Employer> employer = employerService.findEmployerById(id);
        return modelMapper.map(employer, (Type) EmployerDto.class);
    }
    @PostMapping("/saveEmployer")
    public EmployerDto saveEmployer (@Valid @RequestBody EmployerDto employerDto){
        //return employerService.saveEmployer(employer);
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        Employer employer = employerService.saveEmployer(employerRequest);
        return modelMapper.map(employer, EmployerDto.class);
    }
    @PutMapping("/updateEmployer")
    public EmployerDto updateEmployer(@RequestBody EmployerDto employerDto) throws Exception {
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        Employer employerResponse = employerService.updateEmployer(employerRequest);
        return modelMapper.map(employerResponse, EmployerDto.class);
    }
    @DeleteMapping("/delete")
    public void deleteEmployer(@Valid @RequestBody EmployerDto employerDto){
        //employerService.deleteEmployer(employer);
        Employer employerRequest = modelMapper.map(employerDto, Employer.class);
        employerService.deleteEmployer(employerRequest);
    }
}
