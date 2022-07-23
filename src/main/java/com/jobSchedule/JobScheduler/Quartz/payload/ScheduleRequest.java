package com.jobSchedule.JobScheduler.Quartz.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class ScheduleRequest {
    @NotNull
    private LocalDateTime localDateTime;
    private String jobText;
    private String jobAlertMode;
    private String jobDestination;
    private String jobDestinationValue;
    private boolean isRepeated = false;
}
