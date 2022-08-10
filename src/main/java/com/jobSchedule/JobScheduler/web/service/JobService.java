package com.jobSchedule.JobScheduler.web.service;

import com.jobSchedule.JobScheduler.quartz.Scheduling;
import com.jobSchedule.JobScheduler.quartz.payload.ScheduleResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class JobService {
    private final Scheduling scheduling;

    public JobService(Scheduling scheduling) {
        this.scheduling = scheduling;
    }
    public List<ScheduleResponse> getAllJobs(){
        return scheduling.getAllJobs();
    }
    public ScheduleResponse getOneJob(String jobGroup, String jobKey) {
        return scheduling.getOneJob(jobGroup, jobKey);
    }
}
