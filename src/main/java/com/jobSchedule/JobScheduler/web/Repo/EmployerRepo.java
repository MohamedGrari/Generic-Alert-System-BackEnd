package com.jobSchedule.JobScheduler.web.Repo;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployerRepo extends JpaRepository <Employer, Long> {
    List<Employer> findEmployerByPosition(String position);
}
