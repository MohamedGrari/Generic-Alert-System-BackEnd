package com.jobSchedule.JobScheduler.web.Controller;

import com.jobSchedule.JobScheduler.web.Entity.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.Service.AttributeConfigurationService;
import com.jobSchedule.JobScheduler.web.dto.EmployerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
@RestController
public class AttributeConfigurationController {
    @Autowired
    private AttributeConfigurationService attributeConfigurationService;
    @GetMapping("/attributes")
    public List<AttributeConfiguration> getAttributeConfigurations(){
        return attributeConfigurationService.findAllAttributeConfiguration();
    }
}
