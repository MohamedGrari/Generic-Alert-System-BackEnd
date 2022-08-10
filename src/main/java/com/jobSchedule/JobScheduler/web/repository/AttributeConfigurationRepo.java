package com.jobSchedule.JobScheduler.web.repository;

import com.jobSchedule.JobScheduler.web.model.AttributeConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeConfigurationRepo extends JpaRepository<AttributeConfiguration, Long> {
}
