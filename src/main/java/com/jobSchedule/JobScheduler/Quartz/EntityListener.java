package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import lombok.NoArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@NoArgsConstructor
@Component
public class EntityListener {
    private static RequestFormService requestFormService;
    private static List<RequestForm> requests;
    private static String oldPosition;
    private static String oldStatus;
    private static String oldContractType;
    private static SchedulerFactoryBean schedulerFactory;
    private static final Logger logger = LoggerFactory.getLogger(EntityListener.class);
    @Autowired
    public EntityListener(RequestFormService requestFormService, SchedulerFactoryBean schedulerFactory) {
        EntityListener.requestFormService = requestFormService;
        EntityListener.schedulerFactory = schedulerFactory;
    }

    @PostConstruct
    public void postConstruct() {
        requests = requestFormService.findByEntity("employer");
    }

    @PostLoad
    public void onLoad(Employer employer) {
        oldPosition = employer.getPosition();
        oldStatus = employer.getStatus();
        oldContractType = employer.getContractType();
    }

    @PreUpdate
    public void onUpdate(Employer employer) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();
        String position = employer.getPosition();
        String status = employer.getStatus();
        String contractType = employer.getContractType();
        updater(position, status, contractType, scheduleRequest, scheduling);
    }

    @PrePersist
    public void onPersist(Employer employer) throws SchedulerException {
        Scheduler scheduler = schedulerFactory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();
        persister(employer, scheduleRequest, scheduling);
    }

    private void persister(Employer  employer, ScheduleRequest scheduleRequest, Scheduling scheduling) {
        int hour = 23;
        int minute = 41;
        String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
        for (RequestForm requestForm : requests) {
            if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {continue;}
            switch (requestForm.getAttribute()) {
                case "birthday":
                    switch (requestForm.getWantedAttributeValue()){
                        case "AT":
                            scheduleRequest.setLocalDateTime(employer.getBirthday().atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "BEFORE":
                            scheduleRequest.setLocalDateTime(employer.getBirthday().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "AFTER":
                            scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                    }
                    break;
                case "hireDate":
                    switch (requestForm.getWantedAttributeValue()){
                        case "AT":
                            scheduleRequest.setLocalDateTime(employer.getHireDate().atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "BEFORE":
                            scheduleRequest.setLocalDateTime(employer.getHireDate().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "AFTER":
                            scheduleRequest.setLocalDateTime(employer.getHireDate().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                    }
                    break;
                case "endContract":
                    switch (requestForm.getWantedAttributeValue()){
                        case "AT":
                            scheduleRequest.setLocalDateTime(employer.getEndContract().atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "BEFORE":
                            scheduleRequest.setLocalDateTime(employer.getEndContract().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                        case "AFTER":
                            scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                            runPersisterScheduler(scheduleRequest, scheduling, requestForm);
                            break;
                    }
                    break;

            }
        }
    }
    private void updater(String position, String status, String contractType, ScheduleRequest scheduleRequest, Scheduling scheduling) {
        Long offset = 2L;
        String[] entityCriteriaValues = {position, status, contractType, null};
        boolean positionIsChanged = !Objects.equals(position, oldPosition);
        boolean StatusIsChanged = !Objects.equals(status, oldStatus);
        boolean contractTypeIsChanged = !Objects.equals(contractType, oldContractType);
        for (RequestForm requestForm : requests) {
            if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {continue;}
            String wantedAttributeValue = requestForm.getWantedAttributeValue();
            boolean wantedAttributeValueIsWHATEVER = (Objects.equals(wantedAttributeValue, "WHATEVER"));
            switch (requestForm.getAttribute()) {
                case "position":
                    if (!positionIsChanged) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, position))) {
                        runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);}
                    break;
                case "status":
                    if (!StatusIsChanged) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, status))) {
                        runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm); }
                    break;
                case "contractType":
                    if (!contractTypeIsChanged) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, contractType))) {
                        runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);}
                    break;
                case "WHATEVER":
                    if (!positionIsChanged && !StatusIsChanged && !contractTypeIsChanged) {
                        continue;
                    }
                    runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);
                    break;
            }
        }
    }
    private void runPersisterScheduler(ScheduleRequest scheduleRequest, Scheduling scheduling, RequestForm requestForm) {
        scheduleRequest.setJobText(requestForm.getText());
        scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("5edmet");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }
    private void runUpdaterScheduler(ScheduleRequest scheduleRequest, Scheduling scheduling, Long offset, RequestForm requestForm) {
        scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
        scheduleRequest.setJobText(requestForm.getText());
        scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("5edmet");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }
}