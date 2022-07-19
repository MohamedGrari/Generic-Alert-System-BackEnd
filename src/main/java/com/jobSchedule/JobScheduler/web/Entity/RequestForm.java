package com.jobSchedule.JobScheduler.web.Entity;

import com.jobSchedule.JobScheduler.Quartz.SchedulerClass;
import lombok.*;
import org.quartz.SchedulerException;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
//@EntityListeners(EntityListener.class)
public class RequestForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String entity;
    private String entityCriteria;
    private String entityCriteriaValue;
    private String attribute;
    private String wantedAttributeValue;
    private String alertMode;
    private String destination;
    private String wantedDestinationValue;
    private int dayNumber;
    private String text;

    @PostPersist
    private void onPersist(){
        if (Objects.equals(entity, "employer")){
            Employer.subscribe(this);
        }
    }
    public void onPersist(Employer employer, RequestForm requestForm) throws SchedulerException {
        //List<RequestForm> requests = employer.getRequests();
        SchedulerClass persistClass = new SchedulerClass();
        persistClass.Persister(employer, requestForm);
    }

    public void onUpdate(Employer employer, RequestForm requests) throws SchedulerException {
        SchedulerClass persistClass = new SchedulerClass();
        persistClass.Updater(employer, requests);
    }
}
