package com.jobSchedule.JobScheduler.businessLayer;

import com.jobSchedule.JobScheduler.quartz.Scheduling;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.model.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.model.Employer;
import com.jobSchedule.JobScheduler.web.model.RequestForm;
import com.jobSchedule.JobScheduler.web.service.AttributeConfigurationService;
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

import static com.jobSchedule.JobScheduler.businessLayer.config.SubscribingConfig.employers;
import static com.jobSchedule.JobScheduler.businessLayer.config.SubscribingConfig.requests;

@Service
public class EventHandler {
//    @Value("${scheduler.OFFSET}")
    static final long OFFSET = 2;
//    @Value("${scheduler.HOUR}")
    static final int HOUR = 10;
//    @Value("${scheduler.MINUTE}")
    static final int MINUTE = 10;
    private static Scheduling scheduling;
    public static Map<String, String> attributes = new HashMap<>();
    public static List<String> dateAttributes = new ArrayList<>();
    public static List<String> stringAttributes = new ArrayList<>();
    //todo
    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    public EventHandler(AttributeConfigurationService attributeConfigurationService, Scheduling scheduling) {
        EventHandler.scheduling = scheduling;
        List<AttributeConfiguration> attributeConfigurations = attributeConfigurationService.findAllAttributeConfiguration();
        for (AttributeConfiguration attributeConfiguration : attributeConfigurations){
            attributes.put(attributeConfiguration.getAttributeName(), attributeConfiguration.getAttributeType());
            if (Objects.equals(attributeConfiguration.getAttributeType(), "String")){
                stringAttributes.add(attributeConfiguration.getAttributeName());
            } else if (Objects.equals(attributeConfiguration.getAttributeType(), "Date")) {
                dateAttributes.add(attributeConfiguration.getAttributeName());
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
            List<String> entityCriteriaValues = newStringAttributeValues;
            entityCriteriaValues.add(null);
            for (RequestForm requestForm : requests) {
                if (!requestForm.isUpdate()) {continue;}
                if (!entityCriteriaValues.contains(requestForm.getEntityCriteriaValue())) {continue;}
                onUpdate(employer, requestForm, newStringAttributeValues);
                return;
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
            if(isItMatching(employer, request)){continue;}
            onPersist(employer, request);
        }
    }
    private static void onPersist(Employer employer, RequestForm request) {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        LocalDate localDate = (LocalDate)invokeGetter(employer, request.getAttribute());
        if (Objects.equals(request.getDestination(), "AUTO")){request.setDestinationValue(Long.toString(employer.getId()));}
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
            System.out.println("ERROR = " + "localDate is Null" + exception.getMessage());
        }
    }
    public static void handleRequestFormPersisting(RequestForm requestForm){
        if(requestForm.isUpdate()) return;
//        List<Employer> employers = EventHandler.employerService.findAllEmployer();
        for (Employer employer : employers) {
            String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
            if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {continue;}
            onPersist(employer, requestForm);
        }
    }
    public static void handleEmployerPersisting(Employer employer, List<RequestForm> requests){
        for (RequestForm requestForm : requests) {
            if(requestForm.isUpdate()) return;
            if (isItMatching(employer, requestForm)) continue;
            onPersist(employer, requestForm);
        }
    }
    public static boolean isItMatching(Employer employer, RequestForm requestForm){
        List<String> entityCriteriaValues = new ArrayList<>();
        for (String  stringAttribute : stringAttributes){
            entityCriteriaValues.add((String) invokeGetter(employer, stringAttribute));
        }
        entityCriteriaValues.add(null);
        return !entityCriteriaValues.contains(requestForm.getEntityCriteriaValue());
    }
    private static void runScheduler(RequestForm request, Employer employer, ScheduleRequest scheduleRequest) {
        scheduleRequest.setJobText(request.getText());
        scheduleRequest.setJobAlertMode(request.getAlertMode());
        scheduleRequest.setJobDestination(request.getDestination());
        scheduleRequest.setJobDestinationValue(request.getDestinationValue());
        scheduleRequest.setRequestFormId(request.getId());
        scheduleRequest.setEmployerId(employer.getId());
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("IT'S WORKING");
        System.out.println("request = " + request);
        System.out.println("employer = " + employer);
        System.out.println("scheduling = " + scheduleResponse);
        }
    //todo
    public static void handleRequestFormUpdating(RequestForm requestForm) {
        handleRequestFormDeleting(requestForm);
        handleRequestFormPersisting(requestForm);
    }
    //todo
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
            System.out.println(f);
            return f;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }
}