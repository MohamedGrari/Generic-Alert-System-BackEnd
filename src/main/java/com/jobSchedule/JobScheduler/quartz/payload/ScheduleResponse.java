package com.jobSchedule.JobScheduler.quartz.payload;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@ToString
public class ScheduleResponse {
    private boolean success;
    private String message;
    private String jobId;
    private String jobGroup;
    private LocalDate scheduledAt;
    private Date alertTime;
    private Long employerId;
    private Long requestFormId;

    public ScheduleResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ScheduleResponse(boolean success, String message, LocalDate scheduledAt,String jobId, String jobGroup, Date alertTime, Long employerId, Long requestFormId) {
        this.success = success;
        this.message = message;
        this.scheduledAt = scheduledAt;
        this.jobId = jobId;
        this.jobGroup = jobGroup;
        this.alertTime = alertTime;
        this.employerId = employerId;
        this.requestFormId = requestFormId;
    }
}
