package com.jobSchedule.JobScheduler.web.Service;

import com.jobSchedule.JobScheduler.web.Entity.RequestForm;
import com.jobSchedule.JobScheduler.web.Repo.RequestFormRepo;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ListResourceBundle;
import java.util.Optional;
@Transactional
@Service

public class RequestFormService {
    final RequestFormRepo requestFormRepo;
    public RequestFormService(RequestFormRepo requestFormRepo) {
        this.requestFormRepo = requestFormRepo;
    }
    public List<RequestForm> findAllRequests(){
        return requestFormRepo.findAll();
    }
    public Optional<RequestForm> findById(Long id){
        return requestFormRepo.findById(id);
    }
    public RequestForm saveRequestForm(RequestForm requestForm){
        return requestFormRepo.save(requestForm);
    }
    public List<RequestForm> findByEntity(String entity){
        return requestFormRepo.findRequestFormsByEntity(entity);
    }
    public RequestForm updateRequestForm(RequestForm requestForm){
        return requestFormRepo.save(requestForm);
    }
    public void deleteRequestForm(RequestForm requestForm){
        requestFormRepo.delete(requestForm);
    }
}
