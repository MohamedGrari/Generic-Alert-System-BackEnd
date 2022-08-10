package com.jobSchedule.JobScheduler.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String attributeLabel;
    private String attributeName;
    private String attributeType;
    @ManyToOne(optional = false)
    private EntityConfiguration entity;
}
