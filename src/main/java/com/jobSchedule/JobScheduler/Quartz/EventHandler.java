package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Service.EmployerService;
import com.jobSchedule.JobScheduler.web.Service.RequestFormService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
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
    @Autowired
    private RequestFormService requestFormService;
    @Autowired
    private EmployerService employerService;
    private static final List<RequestForm> requests = new ArrayList<>();
    private static final List<Employer> employers = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(EventHandler.class);

    @PostConstruct
    public void subscribing(){
        List<RequestForm> requests = requestFormService.findByEntity("employer");
        List<Employer> employers = employerService.findAllEmployer();
        for( RequestForm request : requests){
            subscribe(request);
        }
        for( Employer employer : employers){
            subscribe(employer);
        }
    }

    public static void subscribe(Employer employer){
        employers.add(employer);
    }
    public static void subscribe(RequestForm requestForm){
        requests.add(requestForm);
    }
    public static void unSubscribe(RequestForm requestForm){
        for(RequestForm requestForm1 : requests){
            if (!Objects.equals(requestForm1.getId(), requestForm.getId())){
                continue;
            } else {
                requests.remove(requestForm1);
                return;
            }
        }
    }
    public static void unSubscribe(Employer employer){
        for(Employer employer1 : employers){
            if (!Objects.equals(employer1.getId(), employer.getId())){
                continue;
            } else {
                employers.remove(employer1);
                return;
            }
        }
    }
    public static void updateSubscriber(RequestForm requestForm){
        for(RequestForm requestForm1 : requests){
            if (!Objects.equals(requestForm1.getId(), requestForm.getId())){
                continue;
            } else {
                requests.set(requests.indexOf(requestForm1), requestForm);
                return;
            }
        }
    }
    public static void updateSubscriber(Employer employer){
        for(Employer employer1 : employers){
            if (!Objects.equals(employer1.getId(), employer.getId())){
                continue;
            } else {
                employers.set(employers.indexOf(employer1), employer);
                return;
            }
        }
    }

    public void handleUpdating(Employer employer) {
        //TODO
        List<Object> oldAttributeValues = EntityListener.getOldAttributes();
//        String oldPosition = EntityListener.getOldPosition();
//        String oldStatus = EntityListener.getOldStatus();
//        String oldContractType = EntityListener.getOldContractType();
        LocalDate oldEndContract = EntityListener.getOldEndContract();
        //TODO
        List<Object> newAttributeValues = new ArrayList<>();
        List<String> attributes = EntityListener.getAttributes();
        for (String attribute :attributes) {
            newAttributeValues.add(invokeGetter(employer, attribute));
        }
//        String positionattribute
//        boolean positionIsChanged = !Objects.equals(position, oldPosition);
//        boolean statusIsChanged = !Objects.equals(status, oldStatus);
//        boolean contractTypeIsChanged = !Objects.equals(contractType, oldContractType);
        boolean endContractIsChanged = !Objects.equals(employer.getEndContract(), oldEndContract);
        //TODO
        if(newAttributeValues == oldAttributeValues && !endContractIsChanged)return;
        for (RequestForm request : requests) {
            //TODO
            if (endContractIsChanged && Objects.equals(request.getAttribute(), "endContract")){
                    handleRequestFormUpdating(request);
            }
            //TODO
            if(!request.isUpdate()){continue;}
            if (!(oldAttributeValues == newAttributeValues)){
                onUpdate(employer, request, oldAttributeValues, newAttributeValues, attributes);
                //TODO
//                if(positionIsChanged && Objects.equals(request.getEntityCriteria(), "position") ||
//                        statusIsChanged && Objects.equals(request.getEntityCriteria(), "status") ||
//                        contractTypeIsChanged && Objects.equals(request.getEntityCriteria(), "contractType"))
//                    handleEmployerUpdating(employer);
            }
        }
    }

    private void onUpdate(Employer employer, RequestForm request, List<Object> oldAttributeValues, List<Object> newAttributeValues, List<String> attributes) {
        if (Objects.equals(request.getDestination(), "AUTO")) {
            request.setDestinationValue(Long.toString(employer.getId()));
        }
        String wantedAttributeValue = request.getWantedAttributeValue();
        boolean wantedAttributeValueIsWHATEVER = (Objects.equals(wantedAttributeValue, "WHATEVER"));
        scheduleRequest.setLocalDateTime(LocalDateTime.now().plusMinutes(OFFSET));
        //TODO
        String value = (String) invokeGetter(employer, request.getAttribute());
        int index = attributes.indexOf(request.getAttribute());
        if (Objects.equals(request.getAttribute(), "WHATEVER")) {
            runScheduler( request, employer);
            return;
        }
        if (oldAttributeValues.get(index) == newAttributeValues.get(index)) {
            return;
        }
        if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, value))) {
            runScheduler(request, employer);
//            handleEmployerUpdating(employer);
        }
    }
//        switch (request.getAttribute()) {
//            case "position":
//                if (!positionIsChanged) {return;}
//                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, employer.getPosition()))) {
//                    runScheduler(request, employer);}
//                break;
//            case "status":
//                if (!statusIsChanged) {return;}
//                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, employer.getStatus()))) {
//                    runScheduler(request, employer); }
//                break;
//            case "contractType":
//                if (!contractTypeIsChanged) {return;}
//                if ((wantedAttributeValueIsWHATEVER) || (Objects.equals(wantedAttributeValue, employer.getContractType()))) {
//                    runScheduler(request, employer);}
//                break;
//            case "WHATEVER":
//                if (!positionIsChanged && !statusIsChanged && !contractTypeIsChanged) {
//                    return;
//                }
//                runScheduler(request, employer);
//                break;
//        }
//    }

    public void handlePersisting(Employer employer){
//        String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
        for (RequestForm request : requests) {
            if(request.isUpdate()) continue;
//            if (!Arrays.asList(entityCriteriaValues).contains(request.getEntityCriteriaValue())) {continue;}
            onPersist(employer, request);
        }
    }

    public Object invokeGetter(Object obj, String variableName)
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
    private void onPersist(Employer employer, RequestForm request) {
        if (Objects.equals(request.getDestination(), "AUTO")){request.setDestinationValue(Long.toString(employer.getId()));}
        if (Objects.equals(request.getAttribute(), "birthday") || Objects.equals(request.getAttribute(), "hireDate")){
            scheduleRequest.setRepeated(true);
        }
        LocalDate localDate = (LocalDate)invokeGetter(employer, request.getAttribute());
        switch (request.getWantedAttributeValue()){
            case "AT":
                scheduleRequest.setLocalDateTime(localDate.withYear(LocalDate.now().getYear()).atTime(HOUR, MINUTE));
                runScheduler(request, employer);
                break;
            case "BEFORE" :
                scheduleRequest.setLocalDateTime(localDate.withYear(LocalDate.now().getYear()).minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                runScheduler(request, employer);
                break;
            case "AFTER" :
                scheduleRequest.setLocalDateTime(localDate.withYear(LocalDate.now().getYear()).plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
                runScheduler(request, employer);
                break;
        }
//        switch (request.getAttribute()) {
//            case "birthday":
//                scheduleRequest.setRepeated(true);
//                switch (request.getWantedAttributeValue()){
//                    case "AT":
//                        scheduleRequest.setLocalDateTime(employer.getBirthday().withYear(LocalDate.now().getYear()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "BEFORE":
//                        scheduleRequest.setLocalDateTime(employer.getBirthday().withYear(LocalDate.now().getYear()).minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "AFTER":
//                        scheduleRequest.setLocalDateTime(employer.getBirthday().withYear(LocalDate.now().getYear()).plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                }
//                break;
//            case "hireDate":
//                scheduleRequest.setRepeated(true);
//                switch (request.getWantedAttributeValue()){
//                    case "AT":
//                        scheduleRequest.setLocalDateTime(employer.getHireDate().withYear(LocalDate.now().getYear()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "BEFORE":
//                        scheduleRequest.setLocalDateTime(employer.getHireDate().withYear(LocalDate.now().getYear()).minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "AFTER":
//                        scheduleRequest.setLocalDateTime(employer.getHireDate().withYear(LocalDate.now().getYear()).plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                }
//                break;
//            case "endContract":
//                switch (request.getWantedAttributeValue()){
//                    case "AT":
//                        scheduleRequest.setLocalDateTime(employer.getEndContract().atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "BEFORE":
//                        scheduleRequest.setLocalDateTime(employer.getEndContract().minusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                    case "AFTER":
//                        scheduleRequest.setLocalDateTime(employer.getEndContract().plusDays(request.getDayNumber()).atTime(HOUR, MINUTE));
//                        runScheduler(request, employer);
//                        break;
//                }
//                break;
//        }
    }

    public void handleRequestFormPersisting(RequestForm requestForm){
        if(requestForm.isUpdate()) return;
        for (Employer employer : employers) {
            String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
            if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {continue;}
            onPersist(employer, requestForm);
        }
    }
    public void handleEmployerPersisting(Employer employer){
        for (RequestForm requestForm : requests) {
            if(requestForm.isUpdate()) return;
            //TODO
            String[] entityCriteriaValues = {employer.getPosition(), employer.getStatus(), employer.getContractType(), null};
            if (!Arrays.asList(entityCriteriaValues).contains(requestForm.getEntityCriteriaValue())) {continue;}
            onPersist(employer, requestForm);
        }
    }
    private void runScheduler(RequestForm request, Employer employer) {
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

    public void handleRequestFormUpdating(RequestForm requestForm) {
        handleRequestFormDeleting(requestForm);
        handleRequestFormPersisting(requestForm);
    }

    public void handleRequestFormDeleting(RequestForm requestForm) {
        List<ScheduleResponse> scheduleResponses = scheduling.getAllJobs();
        for (ScheduleResponse scheduleResponse : scheduleResponses) {
            if (Objects.equals(scheduleResponse.getRequestFormId(), requestForm.getId())) {
                scheduling.deleteJob(scheduleResponse.getJobGroup(), scheduleResponse.getJobId());
            }
        }
    }
    public void handleEmployerDeleting(Employer employer) {
        List<ScheduleResponse> scheduleResponses = scheduling.getAllJobs();
        for (ScheduleResponse scheduleResponse : scheduleResponses) {
            if (Objects.equals(scheduleResponse.getEmployerId(), employer.getId())) {
                scheduling.deleteJob(scheduleResponse.getJobGroup(), scheduleResponse.getJobId());
            }
        }
    }

    public void handleEmployerUpdating(Employer employer) {
        handleEmployerDeleting(employer);
        handleEmployerPersisting(employer);
    }
}