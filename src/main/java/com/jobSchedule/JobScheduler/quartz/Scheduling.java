package com.jobSchedule.JobScheduler.quartz;

import com.jobSchedule.JobScheduler.quartz.jobs.ConsoleJob;
import com.jobSchedule.JobScheduler.quartz.jobs.EmailJob;
import com.jobSchedule.JobScheduler.quartz.jobs.SmsJob;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.service.EmployerService;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Component
public class Scheduling {
    private final JavaMailSender javaMailSender;
    private final EmployerService employerService;
    private final Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(Scheduling.class);

    public Scheduling(JavaMailSender javaMailSender, EmployerService employerService, Scheduler scheduler) {
        this.javaMailSender = javaMailSender;
        this.employerService = employerService;
        this.scheduler = scheduler;
    }
    public ScheduleResponse createSchedule (ScheduleRequest scheduleRequest){
        try {
            LocalDateTime dateTime = scheduleRequest.getLocalDateTime();
            String jobAlertMode = scheduleRequest.getJobAlertMode();
            JobDetail jobDetail = null;
            if(dateTime.isBefore(LocalDateTime.now())) {
                return new ScheduleResponse(false,
                        "dateTime must be after current time");
            }
            switch (jobAlertMode){
                case "CONSOLE" :
                    jobDetail = buildJobDetailCONSOLE(scheduleRequest);
                    break;
                case "EMAIL" :
                    jobDetail = buildJobDetailEMAIL(scheduleRequest);
                    break;
                case "SMS" :
                    jobDetail = buildJobDetailSMS(scheduleRequest);
                    break;
            }
            Trigger trigger = buildJobTrigger(jobDetail, dateTime, scheduleRequest.isRepeated());
            assert jobDetail != null;
            ScheduleResponse scheduleResponse = new ScheduleResponse(true, jobDetail.getKey().getName(),
                    jobDetail.getKey().getGroup(), "Scheduled Successfully!", trigger.getNextFireTime(),
                    scheduleRequest.getEmployerId(), scheduleRequest.getRequestFormId());
            jobDetail.getJobDataMap().put(jobDetail.getKey().getName(), scheduleResponse);
            scheduler.scheduleJob(jobDetail, trigger);
            scheduleResponse.setAlertTime(trigger.getNextFireTime());
            return scheduleResponse;
        } catch (SchedulerException ex) {
            logger.error("Error scheduling ", ex);
            return new ScheduleResponse(false,
                    "Error scheduling. Please try later!");
        }
    }
    private JobDetail buildJobDetailSMS(ScheduleRequest scheduleRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("object", employerService);
        jobDataMap.put("destination", scheduleRequest.getJobDestination());
        jobDataMap.put("destinationValue", scheduleRequest.getJobDestinationValue());
        jobDataMap.put("text", scheduleRequest.getJobText());
        return JobBuilder.newJob(SmsJob.class)
                .withIdentity(UUID.randomUUID().toString(), "SMS_JOBS")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private JobDetail buildJobDetailEMAIL(ScheduleRequest scheduleRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("employerService", employerService);
        jobDataMap.put("mailSender", javaMailSender);
        jobDataMap.put("destination", scheduleRequest.getJobDestination());
        jobDataMap.put("destinationValue", scheduleRequest.getJobDestinationValue());
        jobDataMap.put("text", scheduleRequest.getJobText());
        return JobBuilder.newJob(EmailJob.class)
                .withIdentity(UUID.randomUUID().toString(), "EMAIL_JOBS")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private JobDetail buildJobDetailCONSOLE(ScheduleRequest scheduleRequest) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("text", scheduleRequest.getJobText());
        return JobBuilder.newJob(ConsoleJob.class)
                .withIdentity(UUID.randomUUID().toString(), "CONSOLE_JOBS")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private Trigger buildJobTrigger(JobDetail jobDetail, LocalDateTime startAt, boolean isRepeated) {
        SimpleScheduleBuilder schedule;
        if (isRepeated){
            schedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(8760).repeatForever().withMisfireHandlingInstructionFireNow();
        } else {
            schedule = SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow();
        }
        ZonedDateTime zdt = startAt.atZone(ZoneId.systemDefault());
        Date date = Date.from(zdt.toInstant());
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "my-triggers")
                .startAt(date)
                .withSchedule(schedule)
                .build();
    }
    public List<ScheduleResponse> getAllJobs(){
        try {
            List<ScheduleResponse> scheduleResponses = new ArrayList<>();
            Set<JobKey> jobKeys =  scheduler.getJobKeys(GroupMatcher.anyGroup());
            for (JobKey jobKey : jobKeys){
                try {
                    JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                    ScheduleResponse scheduleResponse = (ScheduleResponse) jobDetail.getJobDataMap().get(jobKey.getName());
                    scheduleResponses.add(scheduleResponse);
                } catch (SchedulerException e){
                    logger.error(e.getMessage(), e);
                }
            }
            return scheduleResponses;
        } catch (SchedulerException e){
            logger.error(e.getMessage());
            return Collections.emptyList();
        }
    }
    public ScheduleResponse getOneJob(String jobGroup, String jobKey){
        try {
            JobDetail jobDetail = scheduler.getJobDetail(new JobKey(jobKey, jobGroup));
            return (ScheduleResponse) jobDetail.getJobDataMap().get(jobKey);
        } catch (SchedulerException | NullPointerException e) {
            logger.error(e.getMessage());
            return null;
        }
    }
    public void deleteJob(String jobGroup, String jobKey){
        try {
            scheduler.deleteJob(new JobKey(jobKey, jobGroup));
        } catch (SchedulerException e) {
            logger.error(e.getMessage());
        }
    }
}


