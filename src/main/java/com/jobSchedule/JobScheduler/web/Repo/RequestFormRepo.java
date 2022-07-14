package com.jobSchedule.JobScheduler.web.Repo;

import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestFormRepo extends JpaRepository<RequestForm, Long> {

    List<RequestForm> findRequestFormsByEntityAndEntityCriteriaValue(String entity, String entityCriteriaValue);
    List<RequestForm> findRequestFormsByEntity(String entity);
    public List<RequestForm> findRequestFormByEntityAndAttribute(String entity, String attribute);
}
