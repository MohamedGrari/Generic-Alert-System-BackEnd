package com.jobSchedule.JobScheduler.web.Service;

import com.jobSchedule.JobScheduler.Quartz.Scheduling;
import com.jobSchedule.JobScheduler.Quartz.payload.ScheduleResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {
    @Autowired
    Scheduling scheduling;
    public List<ScheduleResponse> getAllJobs(){
        return scheduling.getAllJobs();
    }

    public ScheduleResponse getOneJob(String jobGroup, String jobKey) {
        return scheduling.getOneJob(jobGroup, jobKey);
    }
}
