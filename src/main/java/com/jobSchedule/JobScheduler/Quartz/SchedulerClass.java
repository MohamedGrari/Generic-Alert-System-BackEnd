package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class SchedulerClass {
    private static RequestFormService requestFormService;

//    private SchedulerFactoryBean schedulerFactory;
    private static final Logger logger = LoggerFactory.getLogger(SchedulerClass.class);

    public void Persister(Employer employer, RequestForm requestForm) throws SchedulerException {
        int hour = 12;
        int minute = 33;
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();

        String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
        if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {return;}
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

    private void runPersisterScheduler(ScheduleRequest scheduleRequest, Scheduling scheduling, RequestForm requestForm) {
        scheduleRequest.setJobText(requestForm.getText());
        scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("IT'S WORKING");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }

    public void Updater(Employer employer, RequestForm requestForm) throws SchedulerException {
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();

        String position = employer.getPosition();
        String status = employer.getStatus();
        String contractType = employer.getContractType();
        String oldPosition = EntityListener.getOldPosition();
        String oldStatus = EntityListener.getOldStatus();
        String oldContractType = EntityListener.getOldContractType();
        Long offset = 2L;
        String[] entityCriteriaValues = {position, status, contractType, null};
        boolean positionIsChanged = !Objects.equals(position, oldPosition);
        boolean StatusIsChanged = !Objects.equals(status, oldStatus);
        boolean contractTypeIsChanged = !Objects.equals(contractType, oldContractType);

        if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {return;}
        String wantedAttributeValue = requestForm.getWantedAttributeValue();
        boolean wantedAttributeValueIsWHATEVER = (Objects.equals(wantedAttributeValue, "WHATEVER"));
        switch (requestForm.getAttribute()) {
            case "position":
                if (!positionIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, position))) {
                    runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);}
                break;
            case "status":
                if (!StatusIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, status))) {
                    runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm); }
                break;
            case "contractType":
                if (!contractTypeIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, contractType))) {
                    runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);}
                break;
            case "WHATEVER":
                if (!positionIsChanged && !StatusIsChanged && !contractTypeIsChanged) {
                    return;
                }
                runUpdaterScheduler(scheduleRequest,scheduling,offset, requestForm);
                break;
        }
    }

    private void runUpdaterScheduler(ScheduleRequest scheduleRequest, Scheduling scheduling, Long offset, RequestForm requestForm) {
        scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(offset));
        scheduleRequest.setJobText(requestForm.getText());
        scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("IT'S WORKING");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }
}
