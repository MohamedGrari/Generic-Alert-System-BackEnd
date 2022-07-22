package com.jobSchedule.JobScheduler.Quartz;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class SmsJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context){
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String phoneNumber = jobDataMap.getString("number");
        String msgBody = jobDataMap.getString("text");
        try {
            Twilio.init("ACa70269fa83571dd0bfb400dd1f5ec734", "536d201ef6ba879817268bfc0d77f996");
            Message message = Message.creator(new PhoneNumber(phoneNumber), new PhoneNumber("+14782161663"), msgBody).create();
            logger.info("Executing Job with key" + context.getJobDetail().getKey());
            logger.info("Executing scheduler: SMS sent ...");
        } catch (Exception e){
            logger.info("Error while Sending Mail" + e);
        }
    }
}
