package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.Service.EmployerService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
@Component
public class Scheduling {
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    EmployerService employerService;
    private final Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(Scheduling.class);

    @Autowired
    public Scheduling(Scheduler scheduler) {
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
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);
            assert jobDetail != null;
            return new ScheduleResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Scheduled Successfully!");
        } catch (SchedulerException ex) {
            logger.error("Error scheduling email", ex);

            return new ScheduleResponse(false,
                    "Error scheduling email. Please try later!");
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
        jobDataMap.put("object", javaMailSender);
        jobDataMap.put("text", scheduleRequest.getJobText());
        jobDataMap.put("email", "graristar@gmail.com");
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

    private JobDetail buildJobDetail(ScheduleRequest scheduleRequest) {

        JobDataMap jobDataMap = new JobDataMap();

        return JobBuilder.newJob(MyJob.class)
                .withIdentity(UUID.randomUUID().toString(), "my-jobs")
                .withDescription("Send a Job")
                .usingJobData(jobDataMap)
                .storeDurably()
                .build();
    }
    private Trigger buildJobTrigger(JobDetail jobDetail, LocalDateTime startAt) {
        ZonedDateTime zdt = startAt.atZone(ZoneId.systemDefault());
        Date date = Date.from(zdt.toInstant());
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobDetail.getKey().getName(), "my-triggers")
                .withDescription("Send Rest Trigger")
                .startAt(date)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();
    }

}


