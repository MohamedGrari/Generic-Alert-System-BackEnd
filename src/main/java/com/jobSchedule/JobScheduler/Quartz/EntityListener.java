package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class EntityListener {

    @Autowired
    private static RequestFormService requestFormService;

    private static String oldPosition;

    @Autowired
    public void init(RequestFormService requestFormService) {
        this.requestFormService = requestFormService;
    }

//    @PostLoad
//    public void onLoad(Employer employer) {
//        oldPosition = employer.getPosition();
//        System.out.println("oldPosition = " + oldPosition);
//    }

    @PreUpdate
    public void onUpdate(Employer employer) throws SchedulerException {

        Logger logger = LoggerFactory.getLogger(Employer.class);
        logger.info("l update 5edmet mais l  if ma5edmetch");
        //System.out.println("requestForm = " + requestForm);
//        System.out.println("new = " + employer.getPosition());
//        System.out.println("old = " + oldPosition);
//        if (!Objects.equals(oldPosition, employer.getPosition()) && employer.getPosition() == attributeValue) {
//            StdSchedulerFactory factory = new StdSchedulerFactory();
//            Scheduler scheduler = factory.getScheduler();
//            LocalDateTime ldt = employer.getEndContract().atTime(18, 28);
//            System.out.println("ldt = " + ldt);
//            ScheduleRequest scheduleRequest = new ScheduleRequest(ldt);
//            Scheduling scheduling = new Scheduling(scheduler);
//            scheduler.start();
//            ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
//            logger.info("l update 5edmet wel if 5edmet");
//            System.out.println("scheduling = " + scheduleResponse);
//        }
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
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getPosition(), "MANAGER")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "MANAGER");
            System.out.println("requests = " + requests);
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getPosition(), "JUNIOR")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "JUNIOR");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getPosition(), "RH")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "RH");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getPosition(), "INTERN")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "INTERN");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getStatus(), "ON")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "ON");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getStatus(), "OFF")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "OFF");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getContractType(), "CDI")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDI");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getContractType(), "CDD")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDD");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);        }

        if (Objects.equals(employer.getContractType(), "FREELANCE")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "FREELANCE");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

    }

    private void test(Employer employer, ScheduleRequest scheduleRequest, int hour, int minute, List<RequestForm> requests, Logger logger, Scheduling scheduling) {
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
            if (Objects.equals(requestForm.getAttribute(), "birthday")){
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
            if (Objects.equals(requestForm.getAttribute(), "hireDate")){
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
}
