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
    public void deleteJob(String jobGroup, String jobKey){
        scheduling.deleteJob(jobGroup, jobKey);
    }
    public ScheduleResponse getOneJob(String jobGroup, String jobKey) {
        return scheduling.getOneJob(jobGroup, jobKey);
    }
}
