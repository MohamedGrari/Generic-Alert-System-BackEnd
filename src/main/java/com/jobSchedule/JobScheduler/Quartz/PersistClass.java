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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
@Component
public class PersistClass {
    private static RequestFormService requestFormService;

//    private SchedulerFactoryBean schedulerFactory;
    private static final Logger logger = LoggerFactory.getLogger(EntityListener.class);



    public void Persister(Employer employer, List<RequestForm> requests) throws SchedulerException {
        int hour = 11;
        int minute = 29;
        StdSchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        Scheduling scheduling = new Scheduling(scheduler);
        scheduler.start();

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

    private void runPersisterScheduler(ScheduleRequest scheduleRequest, Scheduling scheduling, RequestForm requestForm) {
        scheduleRequest.setJobText(requestForm.getText());
        scheduleRequest.setJobAlertMode(requestForm.getAlertMode());
        System.out.println("scheduleRequest = " + scheduleRequest);
        ScheduleResponse scheduleResponse = scheduling.createSchedule(scheduleRequest);
        logger.info("5edmet");
        System.out.println("requestForm = " + requestForm);
        System.out.println("scheduling = " + scheduleResponse);
    }
}
