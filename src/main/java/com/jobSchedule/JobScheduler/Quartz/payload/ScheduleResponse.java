package com.jobSchedule.JobScheduler.Quartz.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ScheduleResponse {
    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;

    public ScheduleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ScheduleResponse(boolean success, String jobId, String jobGroup, String message) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;
    }
}
