package com.jobSchedule.JobScheduler.web.model;

import com.jobSchedule.JobScheduler.businessLayer.EventHandler;
import jdk.jfr.Event;
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
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handleRequestFormPersisting(this);
    }

    @PreUpdate
    private void onUpdate(){
        EventHandler.updateSubscriber(this);
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handleRequestFormUpdating(this);
    }
    @PreRemove
    private void onRemove(){
        if (Objects.equals(this.getEntity(), "employer")){
            EventHandler.unSubscribe(this);
        }
//        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        EventHandler.handleRequestFormDeleting(this);
    }
}
