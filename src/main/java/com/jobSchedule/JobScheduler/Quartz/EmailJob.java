package com.jobSchedule.JobScheduler.Quartz;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

@Component
public class EmailJob extends QuartzJobBean {
    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
//    @Autowired
//    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    protected void executeInternal(JobExecutionContext context){
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String recipient = jobDataMap.getString("email");
        String msgBody = jobDataMap.getString("text");
        JavaMailSender mailSender = (JavaMailSender) jobDataMap.get("object");
        String subject = "ALERT FROM QUARTZ";
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(sender);
            mailMessage.setTo(recipient);
            mailMessage.setText(msgBody);
            mailMessage.setSubject(subject);
            mailSender.send(mailMessage);
            logger.info("Executing Job with key" + context.getJobDetail().getKey());
            logger.info("Executing scheduler: Email sent ...");
        } catch (Exception e){
            logger.info("Error while Sending Mail" + e);
        }
    }

}
