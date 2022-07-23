package com.jobSchedule.JobScheduler.web.Entity;

import com.jobSchedule.JobScheduler.Quartz.EventHandler;
import lombok.*;
import javax.persistence.*;
import java.util.Objects;

@Entity
@Data
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
    }
}
