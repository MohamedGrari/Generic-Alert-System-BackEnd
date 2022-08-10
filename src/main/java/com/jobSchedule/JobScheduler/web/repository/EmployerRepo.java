package com.jobSchedule.JobScheduler.web.repository;

import com.jobSchedule.JobScheduler.web.model.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerRepo extends JpaRepository <Employer, Long> {
    List<Employer> findEmployersByPosition(String position);
}
