package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Service.EmployerService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SmsJob extends QuartzJobBean {
    @Autowired
    EmployerService employerService;
    private static final String ACCOUNT_SID = "ACa70269fa83571dd0bfb400dd1f5ec734";
    private static final String AUTH_TOKEN = "536d201ef6ba879817268bfc0d77f996";
    private static final String FROM = "+14782161663";
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    @Override
    protected void executeInternal(JobExecutionContext context){
        List<Employer> employers;
        Employer employer;
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String msgBody = jobDataMap.getString("text");
        String jobDestination = jobDataMap.getString("destination");
        String jobDestinationValue = jobDataMap.getString("destinationValue");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        logger.info("Executing Job with key" + context.getJobDetail().getKey());
        switch (jobDestination){
            case "position":
            case "status":
            case "contractType":
                employers = employerService.findEmployerByPosition(jobDestinationValue);
                sendSMS(employers, msgBody);
                break;
            case "ALL":
                employers = employerService.findAllEmployer();
                sendSMS(employers, msgBody);
                break;
            case "AUTO":
            case "ONE":
                employer = employerService.findEmployerById(Long.parseLong(jobDestinationValue)).get();
                sendSMS(employer, msgBody);
                break;
        }
    }

    private void sendSMS(Employer employer, String msgBody) {
        try {
            Message.creator(new PhoneNumber(employer.getPhoneNumber()), new PhoneNumber(FROM), msgBody).create();
            logger.info("Executing scheduler: SMS sent to" + employer.getName());
        } catch (Exception e){
            logger.info("Error while Sending SMS to" + employer.getName() + ":" + e);
        }
    }
    private void sendSMS(List<Employer> employers, String msgBody) {
        for(Employer employer : employers){
            try {
                Message.creator(new PhoneNumber(employer.getPhoneNumber()), new PhoneNumber(FROM), msgBody).create();
                logger.info("Executing scheduler: SMS sent to" + employer.getName());
            } catch (Exception e){
                logger.info("Error while Sending SMS to" + employer.getName() + ":" + e);
            }
        }
    }
}
