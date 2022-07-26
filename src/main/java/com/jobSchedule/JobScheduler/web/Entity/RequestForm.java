package com.jobSchedule.JobScheduler.web.Entity;

import com.jobSchedule.JobScheduler.Quartz.ApplicationContextHolder;
import com.jobSchedule.JobScheduler.Quartz.EventHandler;
import lombok.*;
import javax.persistence.*;
import java.util.Objects;

@Entity
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
    private String destinationValue;
    private String text;
    private int dayNumber;
    private boolean isUpdate;

    @PostPersist
    private void onPersist(){
        if (Objects.equals(entity, "employer")){
            EventHandler.subscribe(this);
        }
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleRequestFormPersisting(this);
    }

    @PreUpdate
    private void onUpdate(){
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleRequestFormUpdating(this);
    }
    @PreRemove
    private void onRemove(){
        if (Objects.equals(entity, "employer")){
            EventHandler.unSubscribe(this);
        }
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleRequestFormDeleting(this);
    }

}
