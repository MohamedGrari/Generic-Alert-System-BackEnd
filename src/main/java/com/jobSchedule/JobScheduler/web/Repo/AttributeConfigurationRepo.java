package com.jobSchedule.JobScheduler.web.Repo;

import com.jobSchedule.JobScheduler.web.Entity.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeConfigurationRepo extends JpaRepository<AttributeConfiguration, Long> {
}
