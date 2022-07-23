package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Component
public class EventHandler {
    @Value("${scheduler.OFFSET}")
    long OFFSET;
    @Value("${scheduler.HOUR}")
    int HOUR;
    @Value("${scheduler.MINUTE}")
    int MINUTE;
    @Autowired
    private ScheduleRequest scheduleRequest;
    @Autowired
    private Scheduling scheduling;
    private static final List<RequestForm> requests = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);
    public static void subscribe(RequestForm requestForm){
        requests.add(requestForm);
    }
    public static void unSubscribe(RequestForm requestForm){
        requests.remove(requestForm);
    }

    public void handleUpdating(Employer employer) {
        for(RequestForm request : requests){
            if (!request.isUpdate()) continue;
            onUpdate(employer, request);
        }
    }

    private void onUpdate(Employer employer, RequestForm request) {
        String position = employer.getPosition();
        String status = employer.getStatus();
        String contractType = employer.getContractType();
        String oldPosition = EntityListener.getOldPosition();
        String oldStatus = EntityListener.getOldStatus();
        String oldContractType = EntityListener.getOldContractType();
        String[] entityCriteriaValues = {position, status, contractType, null};
        boolean positionIsChanged = !Objects.equals(position, oldPosition);
        boolean StatusIsChanged = !Objects.equals(status, oldStatus);
        boolean contractTypeIsChanged = !Objects.equals(contractType, oldContractType);
        if (!Arrays.asList(entityCriteriaValues).contains(request.getEntityCriteriaValue())) return;
        String wantedAttributeValue = request.getWantedAttributeValue();
        boolean wantedAttributeValueIsWHATEVER = (Objects.equals(wantedAttributeValue, "WHATEVER"));
        scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(OFFSET));
        switch (request.getAttribute()) {
            case "position":
                if (!positionIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, position))) {
                    runScheduler(request);}
                break;
            case "status":
                if (!StatusIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, status))) {
                    runScheduler(request); }
                break;
            case "contractType":
                if (!contractTypeIsChanged) {return;}
                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, contractType))) {
                    runScheduler(request);}
                break;
            case "WHATEVER":
                if (!positionIsChanged && !StatusIsChanged && !contractTypeIsChanged) {
                    return;
                }
                runScheduler(request);
                break;
        }
    }

    public void handlePersisting(Employer employer){
        for (RequestForm request : requests) {
            if(request.isUpdate()) continue;
            onPersist(employer, request);
        }
    }

    private void onPersist(Employer employer, RequestForm request) {
        String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
        if (!Arrays.asList(entityCriteriaValues).contains(request.getEntityCriteriaValue())) {return;}
        if (Objects.equals(request.getDestination(), "AUTO")){request.setDestinationValue(Long.toString(employer.getId()));}
        switch (request.getAttribute()) {
            case "birthday":
                switch (request.getWantedAttributeValue()){
                    case "AT":
                        scheduleRequest.setLocalDateTime(employer.getBirthday().atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "BEFORE":
                        scheduleRequest.setLocalDateTime(employer.getBirthday().minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "AFTER":
                        scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                }
                break;
            case "hireDate":
                switch (request.getWantedAttributeValue()){
                    case "AT":
                        scheduleRequest.setLocalDateTime(employer.getHireDate().atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "BEFORE":
                        scheduleRequest.setLocalDateTime(employer.getHireDate().minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "AFTER":
                        scheduleRequest.setLocalDateTime(employer.getHireDate().plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                }
                break;
            case "endContract":
                switch (request.getWantedAttributeValue()){
                    case "AT":
                        scheduleRequest.setLocalDateTime(employer.getEndContract().atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "BEFORE":
                        scheduleRequest.setLocalDateTime(employer.getEndContract().minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                    case "AFTER":
                        scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                        runScheduler(request);
                        break;
                }
                break;
        }
    }

    private void runScheduler(RequestForm request) {
        scheduleRequest.setJobText(request.getText());
        scheduleRequest.setJobAlertMode(request.getAlertMode());
        scheduleRequest.setJobDestination(request.getDestination());
        scheduleRequest.setJobDestinationValue(request.getDestinationValue());
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("IT'S WORKING");
        System.out.println("request = " + request);
        System.out.println("scheduling = " + scheduleResponse);
    }
}

