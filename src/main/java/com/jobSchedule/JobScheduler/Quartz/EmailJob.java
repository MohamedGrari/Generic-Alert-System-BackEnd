//package com.jobSchedule.JobScheduler.Quartz;
//
//import com.jobSchedule.JobScheduler.web.Service.EmployerService;
//import org.quartz.Job;
//import org.quartz.JobDataMap;
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.autoconfigure.mail.MailProperties;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.scheduling.quartz.QuartzJobBean;
//import org.springframework.stereotype.Component;
//
//import javax.mail.internet.MimeMessage;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class EmailJob extends QuartzJobBean {
//    private static final Logger logger = LoggerFactory.getLogger(EmailJob.class);
//    //private final JavaMailSender javaMailSender;
//    @Autowired
//    private JavaMailSender javaMailSender;
//    @Autowired
//    private MailProperties mailProperties;
//    @Value("${spring.mail.username}")
//    private String sender;
//    @Autowired
//    public EmailJob(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }
//    @Override
//    protected void executeInternal(JobExecutionContext context){
//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        String recipient = jobDataMap.getString("email");
//        String msgBody = jobDataMap.getString("text");
//        String subject = "ALERT FROM QUARTZ";
//        sendMail(sender, recipient, msgBody, subject);
//    }
//    public void sendMail(String sender, String recipient, String body, String subject){
//        try {
//            JavaMailSender mailSender = javaMailSender.createMimeMessage();
//            SimpleMailMessage mailMessage = new SimpleMailMessage();
//            mailMessage.setFrom("mohamed.grari@advyteam.com");
//            mailMessage.setTo(recipient);
//            mailMessage.setText(body);
//            mailMessage.setSubject(subject);
//            javaMailSender.send(message);
//            logger.info("Executing Job with key");
//            logger.info("Executing scheduler: Email sent ...");
//        } catch (Exception e){
//            logger.info("Error while Sending Mail" + e);
//        }
//    }
//
//}
