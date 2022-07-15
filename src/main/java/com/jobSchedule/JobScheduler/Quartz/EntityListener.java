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
        //List<String> criteria =;
        //String[] criteria = {employer.getPosition(), employer.getStatus(), employer.getContractType(), "WHATEVER"};
//        Map<String, String> criteria = new HashMap<>();
//        criteria.put("position", employer.getPosition());
//        criteria.put("status", employer.getStatus());
//        criteria.put("contractType", employer.getContractType());
        //if (Objects.equals(position, "SENIOR")) {
        updater(position, status, contractType, scheduleRequest, requests, logger, scheduling);
        //
//        if (Objects.equals(position, "MANAGER")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, position);
//        }
//
//        if (Objects.equals(position, "JUNIOR")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, position);
//        }
//
//        if (Objects.equals(position, "RH")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, position);
//        }
//
//        if (Objects.equals(position, "INTERN")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, position);
//        }
//
//        if (Objects.equals(status, "ON")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, status);
//        }
//
//        if (Objects.equals(status, "OFF")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, status);
//        }
//
//        if (Objects.equals(contractType, "CDI")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);
//        }
//
//        if (Objects.equals(contractType, "CDD")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);
//        }
//
//        if (Objects.equals(contractType, "FREELANCE")) {
//            updater(employer, scheduleRequest, requests, logger, scheduling, contractType);
//
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
        String[] attributeCriteria = {"position", "status", "contractType", "WHATEVER"};
        for (RequestForm requestForm : requests) {
            if (!(Arrays.asList(entityCriteria).contains(requestForm.getEntityCriteriaValue()))) {
                continue;
            }
            if (!(Arrays.asList(attributeCriteria).contains(requestForm.getAttribute()))) {
                continue;
            }
            scheduleRequest.setJobText(requestForm.getText());
            scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
            scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
            scheduleRequest.setLocalDateTime(LocalDateTime.now());
            if (Objects.equals(requestForm.getAttribute(), "WHATEVER")){
                scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                System.out.println("scheduleRequest = " + scheduleRequest);
                ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                logger.info("5edmet");
                System.out.println("requestForm = " + requestForm);
                System.out.println("scheduling = " + scheduleResponse);
                continue;
            }
            if (Objects.equals(requestForm.getWantedAttributeValue(), "WHATEVER")) {
                if ((Objects.equals(requestForm.getAttribute(), "position")) && (!Objects.equals(position, oldPosition))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                    continue;
                } else if ((Objects.equals(requestForm.getAttribute(), "status")) && (!Objects.equals(status, oldStatus))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);

                }else if ((Objects.equals(requestForm.getAttribute(), "contractType")) && (!Objects.equals(contractType, oldContractType))) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                }
            } else {
                if ((Objects.equals(requestForm.getAttribute(), "position")) && (!Objects.equals(position, oldPosition)) && Objects.equals(position, requestForm.getWantedAttributeValue())) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                } else if ((Objects.equals(requestForm.getAttribute(), "status")) && (!Objects.equals(status, oldStatus)) && Objects.equals(status, requestForm.getWantedAttributeValue())) {
                    scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
                    System.out.println("scheduleRequest = " + scheduleRequest);
                    ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
                    logger.info("5edmet");
                    System.out.println("requestForm = " + requestForm);
                    System.out.println("scheduling = " + scheduleResponse);
                } else if ((Objects.equals(requestForm.getAttribute(), "contractType")) && (!Objects.equals(contractType, oldContractType)) && Objects.equals(contractType, requestForm.getWantedAttributeValue())) {
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