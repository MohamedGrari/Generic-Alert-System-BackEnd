package com.jobSchedule.JobScheduler.web.service;

import com.jobSchedule.JobScheduler.web.model.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.repository.AttributeConfigurationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AttributeConfigurationService {
    private AttributeConfigurationRepo repo;
    @Autowired
    public AttributeConfigurationService(AttributeConfigurationRepo repo){
        this.repo = repo;
    }
    public List<AttributeConfiguration> findAllAttributeConfiguration(){
        return repo.findAll();
    }

}
