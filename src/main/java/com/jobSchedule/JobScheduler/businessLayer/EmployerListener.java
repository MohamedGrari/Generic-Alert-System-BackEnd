package com.jobSchedule.JobScheduler.businessLayer;

import com.jobSchedule.JobScheduler.businessLayer.config.SubscribingConfig;
import com.jobSchedule.JobScheduler.web.model.Employer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;

@NoArgsConstructor
@Component
public class EmployerListener {
    public static List<String> oldStringAttributeValues = new ArrayList<>();
    public static List<LocalDate> oldDateAttributeValues = new ArrayList<>();
    @PostLoad
    public void setOldAttributeValues(Employer employer){
        List<String> oldStringAttributeValues = new ArrayList<>();
        List<LocalDate> oldDateAttributeValues = new ArrayList<>();
        for (String dateAttribute : EventHandler.dateAttributes){
            oldDateAttributeValues.add((LocalDate) EventHandler.invokeGetter(employer, dateAttribute));
        }
        for (String stringAttribute : EventHandler.stringAttributes){
            oldStringAttributeValues.add((String) EventHandler.invokeGetter(employer, stringAttribute));
        }
        EmployerListener.oldStringAttributeValues = oldStringAttributeValues;
        EmployerListener.oldDateAttributeValues = oldDateAttributeValues;
    }
    @PostPersist
    public void onPersist(Employer employer){
        SubscribingConfig.subscribe(employer);
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handlePersisting(employer);
    }
    @PreUpdate
    public void onUpdate(Employer employer){
        SubscribingConfig.updateSubscriber(employer);
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handleUpdating(employer);
    }
    @PreRemove
    public void onRemove(Employer employer){
        SubscribingConfig.unSubscribe(employer);
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handleEmployerDeleting(employer);
    }
}