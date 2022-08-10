package com.jobSchedule.JobScheduler.quartz.jobs;

import com.jobSchedule.JobScheduler.web.model.Employer;
import com.jobSchedule.JobScheduler.web.service.EmployerService;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
    private static final String subject = "ALERT FROM QUARTZ";
    @Value("${spring.mail.username}")
    private String sender;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        List<Employer> employers;
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        EmployerService employerService = (EmployerService) jobDataMap.get("employerService");
        JavaMailSender mailSender = (JavaMailSender) jobDataMap.get("mailSender");
        String msgBody = jobDataMap.getString("text");
        String jobDestination = jobDataMap.getString("destination");
        String jobDestinationValue = jobDataMap.getString("destinationValue");
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        switch (jobDestination) {
            case "position":
            case "status":
            case "contractType":
                employers = employerService.findEmployerByPosition(jobDestinationValue);
                for (Employer employer : employers) {
                    sendSMS(employer, msgBody, mailSender, mailMessage);
                }
                break;
            case "ALL":
                employers = employerService.findAllEmployer();
                for (Employer employer : employers) {
                    sendSMS(employer, msgBody, mailSender, mailMessage);
                }
                break;
            case "AUTO":
            case "ONE":
                Employer employer = employerService.findEmployerById(Long.parseLong(jobDestinationValue)).get();
                sendSMS(employer, msgBody, mailSender, mailMessage);
                break;
        }
    }

    private void sendSMS (Employer employer, String msgBody, MailSender mailSender, SimpleMailMessage mailMessage){
        try {
            mailMessage.setFrom(sender);
            mailMessage.setTo(employer.getEmail());
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);
            mailSender.send(mailMessage);
            logger.info("Executing scheduler: SMS sent to " + employer.getName());
        } catch (Exception e) {
            logger.info("Error while Sending SMS to " + employer.getName() + ":" + e);
        }
    }
}
