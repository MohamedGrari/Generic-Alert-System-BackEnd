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
import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Component
public class EntityListener {

    @Autowired
    private static RequestFormService requestFormService;
    private static List<RequestForm> requests;
    private static String oldPosition;
    private static String oldStatus;
    private static String oldContractType;
    //private static List<RequestForm> requests =requestFormService.findByEntityAndEntityCriteriaValue("employer", "SENIOR");

    @Autowired
    public void init(RequestFormService requestFormService) {
        this.requestFormService = requestFormService;

    }

    @PostLoad
    public void onLoad(Employer employer) {
        oldPosition = employer.getPosition();
        oldStatus = employer.getStatus();
        oldContractType = employer.getContractType();
        //System.out.println("oldPosition = " + oldPosition);
    }
    @PostConstruct
    public void contsructor(){
        requests = requestFormService.findByEntity("employer");
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
        if (Objects.equals(position, "SENIOR")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, position);
        }

        if (Objects.equals(position, "MANAGER")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, position);
        }

        if (Objects.equals(position, "JUNIOR")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, position);
        }

        if (Objects.equals(position, "RH")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, position);
        }

        if (Objects.equals(position, "INTERN")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, position);
        }

        if (Objects.equals(status, "ON")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, status);
        }

        if (Objects.equals(status, "OFF")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, status);
        }

        if (Objects.equals(contractType, "CDI")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);
        }

        if (Objects.equals(contractType, "CDD")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);
        }

        if (Objects.equals(contractType, "FREELANCE")) {
            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);

        }
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
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "MANAGER")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "MANAGER");
            System.out.println("requests = " + requests);
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "JUNIOR")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "JUNIOR");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "RH")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "RH");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getPosition(), "INTERN")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "INTERN");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getStatus(), "ON")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "ON");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getStatus(), "OFF")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "OFF");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getContractType(), "CDI")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDI");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getContractType(), "CDD")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "CDD");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }

        if (Objects.equals(employer.getContractType(), "FREELANCE")) {
            List<RequestForm> requests = requestFormService.findByEntityAndEntityCriteriaValue("employer", "FREELANCE");
            test(employer, scheduleRequest, hour, minute, requests, logger, scheduling);
        }
        if (Objects.equals(employer.getPosition(), "ALL")) {
            List<RequestForm> requests = requestFormService.findByEntity("employer");
            System.out.println("requests = " + requests);
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

    private void updater(Employer employer, ScheduleRequest scheduleRequest, List<RequestForm> requests, Logger logger, Scheduling scheduling, String criteria) {
        Long offset = 2L;
        Iterator<RequestForm> iterator = requests.iterator();
        while (iterator.hasNext()) {
            RequestForm requestForm = iterator.next();
            if (Objects.equals(requestForm.getEntityCriteriaValue(), criteria)) {
                scheduleRequest.setJobText(requestForm.getText());
                scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
                scheduleRequest.setLocalDateTime(LocalDateTime.now());
                if ((Objects.equals(requestForm.getAttribute(), "position")) && (!Objects.equals(employer.getPosition(), oldPosition))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                }
                if ((Objects.equals(requestForm.getAttribute(), "status")) && (!Objects.equals(employer.getStatus(), oldStatus))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                }
                if ((Objects.equals(requestForm.getAttribute(), "contractType")) && (!Objects.equals(employer.getContractType(), oldContractType))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                }

            }
        }
    }
}