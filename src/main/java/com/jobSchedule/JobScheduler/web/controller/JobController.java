package com.jobSchedule.JobScheduler.web.controller;

import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JobController {
    @Autowired
    JobService jobService;
    @GetMapping("/jobs")
    public List<ScheduleResponse> getAllJobs(){
        return jobService.getAllJobs();
    }
    @GetMapping("/job/{jobGroup}/{jobKey}")
    public ScheduleResponse getOneJob(@PathVariable String jobGroup, @PathVariable String jobKey){
        return jobService.getOneJob(jobGroup, jobKey);
    }
}
