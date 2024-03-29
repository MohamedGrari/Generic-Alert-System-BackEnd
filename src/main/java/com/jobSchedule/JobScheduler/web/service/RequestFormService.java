package com.jobSchedule.JobScheduler.web.service;

import com.jobSchedule.JobScheduler.web.model.RequestForm;
import com.jobSchedule.JobScheduler.web.repository.RequestFormRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
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
