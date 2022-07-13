package com.jobSchedule.JobScheduler.web.Entity;

//import com.jobSchedule.JobScheduler.Quartz.conf.Config;
import com.jobSchedule.JobScheduler.Quartz.EntityListener;
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
@EntityListeners(EntityListener.class)
public class Employer {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @Email
        private String email;

        private String Position;

        private String status;

        private String contractType;

        private LocalDate hireDate;

        private LocalDate birthday;

        private LocalDate EndContract;

}