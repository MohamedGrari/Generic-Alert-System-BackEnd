package com.jobSchedule.JobScheduler.web.controller;

import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import com.jobSchedule.JobScheduler.web.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class JobController {
    @Autowired
    JobService jobService;
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/jobs")
    public List<ScheduleResponse> getAllJobs(){
        return jobService.getAllJobs();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("/job/{jobGroup}/{jobKey}")
    public ScheduleResponse getOneJob(@PathVariable String jobGroup, @PathVariable String jobKey){
        return jobService.getOneJob(jobGroup, jobKey);
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/job/{jobGroup}/{jobKey}/delete")
    public void deleteJob(@PathVariable String jobGroup, @PathVariable String jobKey){
        jobService.deleteJob(jobGroup, jobKey);
    }
}
