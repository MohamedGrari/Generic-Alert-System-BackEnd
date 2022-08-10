package com.jobSchedule.JobScheduler.web.controller;

import com.jobSchedule.JobScheduler.web.model.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.service.AttributeConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AttributeConfigurationController {
    @Autowired
    private AttributeConfigurationService attributeConfigurationService;
    @GetMapping("/attributes")
    public List<AttributeConfiguration> getAttributeConfigurations(){
        return attributeConfigurationService.findAllAttributeConfiguration();
    }
}
