package com.jobSchedule.JobScheduler.businessLayer;

import com.jobSchedule.JobScheduler.quartz.Scheduling;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.model.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.model.Employer;
import com.jobSchedule.JobScheduler.web.model.RequestForm;
import com.jobSchedule.JobScheduler.web.service.AttributeConfigurationService;
import com.jobSchedule.JobScheduler.web.service.EmployerService;
import com.jobSchedule.JobScheduler.web.service.RequestFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
public class EventHandler {
    static final long OFFSET = 2;
    static final int HOUR = 15;
    static final int MINUTE = 33;
    private static Scheduling scheduling;
    public static Map<String, String> attributes = new HashMap<>();
    public static List<String> dateAttributes = new ArrayList<>();
    public static List<String> stringAttributes = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler(AttributeConfigurationService attributeConfigurationService, Scheduling scheduling, RequestFormService requestFormService, EmployerService employerService) {
        EventHandler.scheduling = scheduling;
        List<AttributeConfiguration> attributeConfigurations = attributeConfigurationService.findAllAttributes();
        for (AttributeConfiguration attributeConfiguration : attributeConfigurations){
            attributes.put(attributeConfiguration.getAttributeName(), attributeConfiguration.getAttributeType());
            if (Objects.equals(attributeConfiguration.getAttributeType(), "String")){
                stringAttributes.add(attributeConfiguration.getAttributeName());
            } else if (Objects.equals(attributeConfiguration.getAttributeType(), "Date")) {
                dateAttributes.add(attributeConfiguration.getAttributeName());
            }
        }
        List<RequestForm> requests = requestFormService.findByEntity("employer");
        List<Employer> employers = employerService.findAllEmployer();
        for( RequestForm request : requests){subscribe(request);}
        for( Employer employer : employers){subscribe(employer);}
    }

    public static final List<RequestForm> requests = new ArrayList<>();
    public static final List<Employer> employers = new ArrayList<>();

    public static void subscribe(Employer employer){
        employers.add(employer);
    }
    public static void subscribe(RequestForm requestForm){
        requests.add(requestForm);
    }
    public static void unSubscribe(RequestForm requestForm){
        requests.removeIf(requestForm1 -> Objects.equals(requestForm1.getId(), requestForm.getId()));
    }
    public static void unSubscribe(Employer employer){
        employers.removeIf(employer1 -> Objects.equals(employer1.getId(), employer.getId()));
    }
    public static void updateSubscriber(RequestForm requestForm){
        for(RequestForm requestForm1 : requests){
            if (Objects.equals(requestForm1.getId(), requestForm.getId())){
                requests.set(requests.indexOf(requestForm1), requestForm);
            }
        }
    }
    public static void updateSubscriber(Employer employer){
        for(Employer employer1 : employers){
            if (Objects.equals(employer1.getId(), employer.getId())){
                employers.set(employers.indexOf(employer1), employer);
            }
        }
    }

    public static void handleUpdating(Employer employer) {
        List<String> newStringAttributeValues = new ArrayList<>();
        List<LocalDate> newDateAttributeValues = new ArrayList<>();
        for (String dateAttribute : EventHandler.dateAttributes) {
            newDateAttributeValues.add((LocalDate) EventHandler.invokeGetter(employer, dateAttribute));
        }
        for (String stringAttribute : EventHandler.stringAttributes) {
            newStringAttributeValues.add((String) EventHandler.invokeGetter(employer, stringAttribute));
        }
        if (!newStringAttributeValues.equals(EmployerListener.oldStringAttributeValues)) {
            handleEmployerUpdating(employer, requests);
//            List<String> entityCriteriaValues = new ArrayList<>();
//            for (String el : newStringAttributeValues){
//                entityCriteriaValues.add(el);
//            }
//            List<String> entityCriteriaValues = newStringAttributeValues;
            newStringAttributeValues.add(null);
            newStringAttributeValues.add("");
            for (RequestForm requestForm : requests) {
                if (!requestForm.isUpdate()) {continue;}
                if ((newStringAttributeValues.contains(requestForm.getEntityCriteriaValue())) || (Objects.equals(requestForm.getEntityCriteria(), requestForm.getAttribute()) && EmployerListener.oldStringAttributeValues.contains(requestForm.getEntityCriteriaValue()))) {
                    onUpdate(employer, requestForm, newStringAttributeValues);
                }
            }
        } else if (!newDateAttributeValues.equals(EmployerListener.oldDateAttributeValues)) {
            handleEmployerUpdating(employer, requests);
        }
    }
    private static void onUpdate(Employer employer, RequestForm requestForm, List<String> newStringAttributeValues) {
            ScheduleRequest scheduleRequest = new ScheduleRequest();
            if (Objects.equals(requestForm.getDestination(), "AUTO")) {
                requestForm.setDestinationValue(Long.toString(employer.getId()));
            }
            boolean wantedAttributeValueIsWHATEVER = (Objects.equals(requestForm.getWantedAttributeValue(), "WHATEVER"));
            scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(OFFSET));
            String value = (String) invokeGetter(employer, requestForm.getAttribute());
            int index = stringAttributes.indexOf(requestForm.getAttribute());
            if (Objects.equals(requestForm.getAttribute(), "WHATEVER")) {
                runScheduler( requestForm, employer, scheduleRequest);
                return;
            }
            if (Objects.equals(newStringAttributeValues.get(index), EmployerListener.oldStringAttributeValues.get(index))) {
                return;
            }
            if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(requestForm.getWantedAttributeValue(), value))) {
                runScheduler(requestForm, employer, scheduleRequest);
            }
        }
    public static void handlePersisting(Employer employer){
        for (RequestForm request : requests) {
            if(request.isUpdate()) continue;
            if(!isItMatching(employer, request)){continue;}
            onPersist(employer, request);
        }
    }
    private static void onPersist(Employer employer, RequestForm request) {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        LocalDate localDate = (LocalDate)invokeGetter(employer, request.getAttribute());
        if (Objects.equals(request.getAttribute(), "birthday") || Objects.equals(request.getAttribute(), "hireDate")){
            scheduleRequest.setRepeated(true);
        }
        try {
            switch (request.getWantedAttributeValue()) {
                case "AT":
                    if (scheduleRequest.isRepeated()) {
                        LocalDateTime date = localDate.withYear(LocalDate.now().getYear()).atTime(HOUR, MINUTE);
                        if (date.isBefore(LocalDateTime.now())) {
                            scheduleRequest.setLocalDateTime(date.plusYears(1));
                        } else {
                            scheduleRequest.setLocalDateTime(date);
                        }
                    } else {
                        scheduleRequest.setLocalDateTime(localDate.atTime(HOUR, MINUTE));
                    }
                    runScheduler(request, employer, scheduleRequest);
                    break;
                case "BEFORE":
                    if (scheduleRequest.isRepeated()) {
                        LocalDateTime date = localDate.withYear(LocalDate.now().getYear()).minusDays(request.getDayNumber()).atTime(HOUR, MINUTE);
                        if (date.isBefore(LocalDateTime.now())) {
                            scheduleRequest.setLocalDateTime(date.plusYears(1));
                        } else {
                            scheduleRequest.setLocalDateTime(date);
                        }
                    } else {
                        scheduleRequest.setLocalDateTime(localDate.minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                    }
                    runScheduler(request, employer, scheduleRequest);
                    break;
                case "AFTER":
                    if (scheduleRequest.isRepeated()) {
                        LocalDateTime date = localDate.withYear(LocalDate.now().getYear()).plusDays(request.getDayNumber()).atTime(HOUR, MINUTE);
                        if (date.isBefore(LocalDateTime.now())) {
                            scheduleRequest.setLocalDateTime(date.plusYears(1));
                        } else {
                            scheduleRequest.setLocalDateTime(date);
                        }
                    } else {
                        scheduleRequest.setLocalDateTime(localDate.plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                    }
                    runScheduler(request, employer, scheduleRequest);
                    break;
            }
        } catch (NullPointerException exception){
            System.out.println("ERROR = localDate is Null : " + exception.getMessage());
        }
    }
    public static void handleRequestFormPersisting(RequestForm requestForm){
        if(requestForm.isUpdate()) return;
//        List<Employer> employers = EventHandler.employerService.findAllEmployer();
        for (Employer employer : employers) {
            if (!isItMatching(employer, requestForm)){continue;}
            onPersist(employer, requestForm);
        }
    }
    public static void handleEmployerPersisting(Employer employer, List<RequestForm> requests){
        for (RequestForm requestForm : requests) {
            if(requestForm.isUpdate()) return;
            if (!isItMatching(employer, requestForm)) continue;
            onPersist(employer, requestForm);
        }
    }
    public static boolean isItMatching(Employer employer, RequestForm requestForm){
        List<String> entityCriteriaValues = new ArrayList<>();
        for (String  stringAttribute : stringAttributes){
            entityCriteriaValues.add((String) invokeGetter(employer, stringAttribute));
        }
        entityCriteriaValues.add(null);
        entityCriteriaValues.add("");
        entityCriteriaValues.add(String.valueOf(employer.getId()));
        return entityCriteriaValues.contains(requestForm.getEntityCriteriaValue());
    }
    private static void runScheduler(RequestForm request, Employer employer, ScheduleRequest scheduleRequest) {
        scheduleRequest.setJobText(request.getText());
        scheduleRequest.setJobAlertMode(request.getAlertMode());
        if (Objects.equals(request.getDestination(), "AUTO")) {
            scheduleRequest.setJobDestinationValue(Long.toString(employer.getId()));
        } else {
            scheduleRequest.setJobDestinationValue(request.getDestinationValue());
        }
        scheduleRequest.setJobDestination(request.getDestination());
        scheduleRequest.setRequestFormId(request.getId());
        scheduleRequest.setEmployerId(employer.getId());
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("SCHEDULED!");
        System.out.println("request = " + request);
        System.out.println("employer = " + employer);
        System.out.println("scheduling = " + scheduleResponse);
    }
    public static void handleRequestFormUpdating(RequestForm requestForm) {
        handleRequestFormDeleting(requestForm);
        handleRequestFormPersisting(requestForm);
    }
    public static void handleRequestFormDeleting(RequestForm requestForm) {
        List<ScheduleResponse> scheduleResponses = scheduling.getAllJobs();
        for (ScheduleResponse scheduleResponse : scheduleResponses) {
            if (Objects.equals(scheduleResponse.getRequestFormId(), requestForm.getId())) {
                scheduling.deleteJob(scheduleResponse.getJobGroup(), scheduleResponse.getJobId());
            }
        }
    }
    public static void handleEmployerDeleting(Employer employer) {
        List<ScheduleResponse> scheduleResponses = scheduling.getAllJobs();
        for (ScheduleResponse scheduleResponse : scheduleResponses) {
            if(scheduleResponse.getAlertTime().toInstant().isBefore(Instant.now())){continue;}
            if (Objects.equals(scheduleResponse.getEmployerId(), employer.getId())) {
                scheduling.deleteJob(scheduleResponse.getJobGroup(), scheduleResponse.getJobId());
            }
        }
    }
    public static void handleEmployerUpdating(Employer employer, List<RequestForm> requests) {
        handleEmployerDeleting(employer);
        handleEmployerPersisting(employer, requests);
    }
    public static Object invokeGetter(Object obj, String variableName)
    {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
            Method getter = pd.getReadMethod();
            Object f = getter.invoke(obj);
            return f;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }
}