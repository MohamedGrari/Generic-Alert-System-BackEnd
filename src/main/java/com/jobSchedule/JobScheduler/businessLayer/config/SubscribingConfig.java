package com.jobSchedule.JobScheduler.businessLayer.config;

import com.jobSchedule.JobScheduler.web.model.Employer;
import com.jobSchedule.JobScheduler.web.model.RequestForm;
import com.jobSchedule.JobScheduler.web.service.EmployerService;
import com.jobSchedule.JobScheduler.web.service.RequestFormService;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
@Component
public class SubscribingConfig {
    public static final List<RequestForm> requests = new ArrayList<>();
    public static final List<Employer> employers = new ArrayList<>();

    public SubscribingConfig(EmployerService employerService, RequestFormService requestFormService) {
        List<RequestForm> requests = requestFormService.findByEntity("employer");
        List<Employer> employers = employerService.findAllEmployer();
        for( RequestForm request : requests){subscribe(request);}
        for( Employer employer : employers){subscribe(employer);}
    }
    public static void subscribe(Employer employer){
        employers.add(employer);
    }
    public static void subscribe(RequestForm requestForm){
        requests.add(requestForm);
    }
    public static void unSubscribe(RequestForm requestForm){
        requests.removeIf(requestForm1 -> Objects.equals(requestForm1.getId(), requestForm.getId()));
    }
    public static void unSubscribe(Employer employer){
        employers.removeIf(employer1 -> Objects.equals(employer1.getId(), employer.getId()));
    }
    public static void updateSubscriber(RequestForm requestForm){
        for(RequestForm requestForm1 : requests){
            if (Objects.equals(requestForm1.getId(), requestForm.getId())){
                requests.set(requests.indexOf(requestForm1), requestForm);
            }
        }
    }
    public static void updateSubscriber(Employer employer){
        for(Employer employer1 : employers){
            if (Objects.equals(employer1.getId(), employer.getId())){
                employers.set(employers.indexOf(employer1), employer);
            }
        }
    }
}
