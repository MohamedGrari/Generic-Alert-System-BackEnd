package com.jobSchedule.JobScheduler.web.Entity;

//import com.jobSchedule.JobScheduler.Quartz.conf.Config;
import com.jobSchedule.JobScheduler.Quartz.EntityListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.quartz.SchedulerException;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        @Transient
        private static List<RequestForm> requests = new ArrayList<>();

        public static void subscribe(RequestForm requestForm){
                requests.add(requestForm);
        }
        public void unSubscribe(RequestForm requestForm){
                requests.remove(requestForm);
        }
        public  void notifySubscribers(Employer employer) throws SchedulerException {
                System.out.println("requests = " + requests);
                for (RequestForm requestForm  : requests){
                        String[] persistEntityAttributes= {"endContract", "hireDate", "birthday"};
                        if(Arrays.asList(persistEntityAttributes).contains(requestForm.getAttribute())){
                                requestForm.onPersist(employer, requestForm);
                        }
                        else {
                                requestForm.onUpdate(employer, requestForm);
                        }

                }
        }

}