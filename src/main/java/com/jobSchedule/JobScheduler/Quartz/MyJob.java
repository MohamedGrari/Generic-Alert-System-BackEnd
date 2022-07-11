package com.jobSchedule.JobScheduler.Quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.LocalDateTime;

public class MyJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(MyJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());
        logger.info("Executing scheduler: " + LocalDateTime.now());
    }
}
