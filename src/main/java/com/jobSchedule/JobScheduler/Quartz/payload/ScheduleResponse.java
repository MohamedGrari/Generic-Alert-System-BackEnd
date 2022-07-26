package com.jobSchedule.JobScheduler.Quartz.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@ToString
public class ScheduleResponse {
    private boolean success;
    private String jobId;
    private String jobGroup;
    private String message;
    private Date alertTime;
    private Long employerId;
    private Long requestFormId;

    public ScheduleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ScheduleResponse(boolean success, String jobId, String jobGroup, String message, Date alertTime, Long employerId, Long requestFormId) {
        this.success = success;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.message = message;
        this.alertTime = alertTime;
        this.employerId = employerId;
        this.requestFormId = requestFormId;
    }
}
