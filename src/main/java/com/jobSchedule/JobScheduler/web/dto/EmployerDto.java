package com.jobSchedule.JobScheduler.web.dto;

import lombok.Data;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Email;
import java.time.LocalDate;
@Data
@Component
public class EmployerDto {
    private Long id;

    private String name;

    @Email
    private String email;
    private String phoneNumber;
    private String Position;

    private String status;

    private String contractType;

    private LocalDate hireDate;

    private LocalDate birthday;

    private LocalDate EndContract;
}
