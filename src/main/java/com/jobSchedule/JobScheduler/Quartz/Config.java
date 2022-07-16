package com.jobSchedule.JobScheduler.Quartz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class Config {
    @Bean
    public SchedulerFactoryBean createScheduler(){
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        //schedulerFactory.setSchedulerName("MyScheduler");
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));
        return schedulerFactory;
    }
}
