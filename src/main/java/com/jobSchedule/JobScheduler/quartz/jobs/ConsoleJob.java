package com.jobSchedule.JobScheduler.quartz.jobs;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class ConsoleJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(ConsoleJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context) {
        logger.info("Executing Job with key {}", context.getJobDetail().getKey());
        logger.info("Executing scheduler: " + context.getJobDetail().getJobDataMap().getString("text"));
    }
}
