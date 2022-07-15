package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import lombok.NoArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public EntityListener(RequestFormService requestFormService) {
        EntityListener.requestFormService = requestFormService;
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
        Logger logger = LoggerFactory.getLogger(Employer.class);
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        System.out.println("requests = " + requests);
        scheduler.start();

        String position = employer.getPosition();
        String status = employer.getStatus();
        String contractType = employer.getContractType();
        updater(position, status, contractType, scheduleRequest, requests, logger, scheduling);
    }

    @PrePersist
    public void onPersist(Employer employer) throws SchedulerException {
        Logger logger = LoggerFactory.getLogger(Employer.class);
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();
        int hour = 23;
        int minute = 41;
        if (Objects.equals(employer.getPosition(), "SENIOR")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "SENIOR");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "MANAGER")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "MANAGER");
            System.out.println("requests = " + requests);
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "JUNIOR")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "JUNIOR");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "RH")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "RH");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "INTERN")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "INTERN");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getStatus(), "ON")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "ON");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getStatus(), "OFF")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "OFF");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getContractType(), "CDI")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDI");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getContractType(), "CDD")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDD");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }
        if (Objects.equals(employer.getContractType(), "FREELANCE")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "FREELANCE");
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }
        if (Objects.equals(employer.getPosition(), "ALL")) {
            List<RequestForm> requests = requestFormService.findByEntity("employer");
            System.out.println("requests = " + requests);
            persister(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }
    }

    private void persister(Employer employer, ScheduleRequest scheduleRequest, int hour, int minute, List<RequestForm> requests, Logger logger, Scheduling scheduling) {
        for (RequestForm requestForm : requests) {
            scheduleRequest.setJobText(requestForm.getText());
            scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
            if (Objects.equals(requestForm.getAttribute(), "endContract")) {
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AT")) {
                    scheduleRequest.setLocalDateTime(employer.getEndContract().atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "BEFORE")) {
                    scheduleRequest.setLocalDateTime(employer.getEndContract().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AFTER")) {
                    scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
            }
            if (Objects.equals(requestForm.getAttribute(), "birthday")) {
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AT")) {
                    scheduleRequest.setLocalDateTime(employer.getBirthday().atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "BEFORE")) {
                    scheduleRequest.setLocalDateTime(employer.getBirthday().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AFTER")) {
                    scheduleRequest.setLocalDateTime(employer.getBirthday().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
            }
            if (Objects.equals(requestForm.getAttribute(), "hireDate")) {
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AT")) {
                    scheduleRequest.setLocalDateTime(employer.getHireDate().atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "BEFORE")) {
                    scheduleRequest.setLocalDateTime(employer.getHireDate().minusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
                if (Objects.equals(requestForm.getWantedAttributeValue(), "AFTER")) {
                    scheduleRequest.setLocalDateTime(employer.getHireDate().plusDays(requestForm.getDayNumber()).atTime(hour, minute));
                }
            }
            ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
            logger.info("5edmet");
            System.out.println("requestForm = " + requestForm);
            System.out.println("scheduling = " + scheduleResponse);
        }
    }

    private void updater(String position, String status, String contractType, ScheduleRequest scheduleRequest, List<RequestForm> requests, Logger logger, Scheduling scheduling) {
        Long offset = 2L;
        String[] entityCriteria = {position, status, contractType, null};
        boolean isNotChangedPosition = Objects.equals(position, oldPosition);
        boolean isNotChangedStatus = Objects.equals(status, oldStatus);
        boolean isNotChangedContractType = Objects.equals(contractType, oldContractType);
        for (RequestForm requestForm : requests) {
            String wantedAttributeValue = requestForm.getWantedAttributeValue();
            boolean wantedAttributeValueIsWHATEVER = (Objects.equals(wantedAttributeValue, "WHATEVER"));
            if (!Arrays.asList(entityCriteria).contains(requestForm.getEntityCriteriaValue())) {continue;}
            switch (requestForm.getAttribute()) {
                case "position":
                    if (isNotChangedPosition) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, position))) {
                        runScheduler(scheduleRequest,logger,scheduling,offset, requestForm);}
                    break;
                case "status":
                    if (isNotChangedStatus) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, status))) {
                        runScheduler(scheduleRequest,logger,scheduling,offset, requestForm); }
                    break;
                case "contractType":
                    if (isNotChangedContractType) {continue;}
                    if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, contractType))) {
                        runScheduler(scheduleRequest,logger,scheduling,offset, requestForm);}
                    break;
                case "WHATEVER":
                    if (isNotChangedPosition && isNotChangedStatus && isNotChangedContractType) {
                        continue;
                    }
                    runScheduler(scheduleRequest,logger,scheduling,offset, requestForm);
                    break;
            }
        }
    }

    private void runScheduler(ScheduleRequest scheduleRequest, Logger logger, Scheduling scheduling, Long offset, RequestForm requestForm) {
        scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("5edmet");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }
}
