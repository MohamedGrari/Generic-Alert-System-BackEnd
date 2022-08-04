package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;
import java.time.LocalDate;

@NoArgsConstructor
@Component
public class EntityListener {
    private static LocalDate oldEndContract;
    private static String oldPosition;
    private static String oldStatus;
    private static String oldContractType;
    //TODO
    public static LocalDate getOldEndContract() {
        return oldEndContract;
    }
    public static String getOldPosition() {
        return oldPosition;
    }
    public static String getOldStatus() {
        return oldStatus;
    }
    public static String getOldContractType() {
        return oldContractType;
    }

    @PostLoad
    public void onLoad(Employer employer) {
        //TODO
        oldPosition = employer.getPosition();
        oldStatus = employer.getStatus();
        oldContractType = employer.getContractType();
        oldEndContract = employer.getEndContract();
    }
    @PostPersist
    public void notifySubForPersist(Employer employer){
        EventHandler.subscribe(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handlePersisting(employer);
    }
    @PreUpdate
    public void notifySubForUpdate(Employer employer){
        EventHandler.updateSubscriber(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleUpdating(employer);
//        eventHandler.handleEmployerUpdating(employer);
    }
    @PreRemove
    public void unsubscribe(Employer employer){
        EventHandler.unSubscribe(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleEmployerDeleting(employer);
    }
}