package com.jobSchedule.JobScheduler.web.repository;

import com.jobSchedule.JobScheduler.web.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployerRepo extends JpaRepository <Employer, Long> {
}
