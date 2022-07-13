package com.jobSchedule.JobScheduler.web.Service;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Repo.EmployerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmployerService {
    final EmployerRepo employerRepo;
    @Autowired
    public EmployerService(EmployerRepo employerRepo) {
        this.employerRepo = employerRepo;
    }
    public Optional<Employer> findEmployerById (Long id){
        return employerRepo.findById(id);
    }
    public List<Employer> findAllEmployer (){
        return employerRepo.findAll();
    }
    public Employer saveEmployer(Employer employer){
        return employerRepo.save(employer);
    }
    public Employer updateEmployer(Employer employer) {
        return employerRepo.save(employer);
    }
    public void deleteEmployer(Employer employer){
        employerRepo.delete(employer);
    }
}
