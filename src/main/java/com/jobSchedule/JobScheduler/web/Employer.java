package com.jobSchedule.JobScheduler.web;

import com.jobSchedule.JobScheduler.Quartz.MyJob;
import com.jobSchedule.JobScheduler.Quartz.Scheduling;
import com.jobSchedule.JobScheduler.Quartz.conf.Config;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employer {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Email
        private String email;

        private String Position;

        private String status;

        private String contractType;

        private LocalDate hireDate;

        private LocalDate birthday;

        private LocalDate EndContract;

        @PostPersist
        public void onPersist() throws SchedulerException, IOException {
                Config config = new Config();
                Logger logger = LoggerFactory.getLogger(Employer.class);
//                StdSchedulerFactory factory = new StdSchedulerFactory();
//                Scheduler scheduler = factory.getScheduler();
//
                Scheduler scheduler = config.scheduler();
                ScheduleRequest scheduleRequest = new ScheduleRequest(LocalDateTime.now());
                Scheduling scheduling = new Scheduling(null) ;
                scheduler.start();
                scheduling.createSchedule(scheduleRequest);
                logger.info("5edmet");
        }
    }
