package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class AdderClass {
    @Autowired
    RequestFormService requestFormService;
    @PostConstruct
    public void adder(){
        List<RequestForm> requests = requestFormService.findByEntity("employer");
        for( RequestForm request : requests){
            Employer.subscribe(request);
        }
    }
}
