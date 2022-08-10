package com.jobSchedule.JobScheduler.web.repository;

import com.jobSchedule.JobScheduler.web.model.RequestForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestFormRepo extends JpaRepository<RequestForm, Long> {
    List<RequestForm> findRequestFormsByEntity(String entity);

}
