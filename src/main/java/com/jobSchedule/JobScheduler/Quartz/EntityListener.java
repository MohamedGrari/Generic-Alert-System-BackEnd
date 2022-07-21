package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import javax.persistence.*;

@NoArgsConstructor
@Component
public class EntityListener {
    private static String oldPosition;
    private static String oldStatus;
    private static String oldContractType;

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
        oldPosition = employer.getPosition();
        oldStatus = employer.getStatus();
        oldContractType = employer.getContractType();
    }

    @PrePersist
    public void notifySubForPersist(Employer employer){
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handlePersisting(employer);
    }
    @PreUpdate
    public void notifySubForUpdate(Employer employer){
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleUpdating(employer);
    }
}