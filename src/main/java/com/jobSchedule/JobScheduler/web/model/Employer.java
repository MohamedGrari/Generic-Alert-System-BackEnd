package com.jobSchedule.JobScheduler.web.model;

import com.jobSchedule.JobScheduler.businessLayer.EmployerListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(EmployerListener.class)
public class Employer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String name;
        @Email
        private String email;
        private String phoneNumber;
        private String position;
        private String status;
        private String contractType;
        private LocalDate hireDate;
        private LocalDate birthday;
        private LocalDate endContract;
}