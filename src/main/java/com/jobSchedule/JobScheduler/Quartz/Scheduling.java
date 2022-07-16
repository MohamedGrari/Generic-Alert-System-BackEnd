package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.UUID;
@Component
public class Scheduling {
    private final Scheduler scheduler;
    private static final Logger logger = LoggerFactory.getLogger(Scheduling.class);

    @Autowired
    public Scheduling(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public ScheduleResponse createSchedule (ScheduleRequest scheduleRequest){
        try {
            LocalDateTime dateTime = scheduleRequest.getLocalDateTime();
            if(dateTime.isBefore(LocalDateTime.now())) {
                return new ScheduleResponse(false,
                        "dateTime must be after current time");
            }

            JobDetail jobDetail = buildJobDetail(scheduleRequest);
            Trigger trigger = buildJobTrigger(jobDetail, dateTime);
            scheduler.scheduleJob(jobDetail, trigger);

            return new ScheduleResponse(true,
                    jobDetail.getKey().getName(), jobDetail.getKey().getGroup(), "Scheduled Successfully!");
        } catch (SchedulerException ex) {
            logger.error("Error scheduling email", ex);

            return new ScheduleResponse(false,
                    "Error scheduling email. Please try later!");
        }
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


