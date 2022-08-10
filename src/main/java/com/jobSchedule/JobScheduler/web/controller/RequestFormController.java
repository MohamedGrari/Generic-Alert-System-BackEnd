package com.jobSchedule.JobScheduler.web.controller;

import com.jobSchedule.JobScheduler.web.model.RequestForm;
import com.jobSchedule.JobScheduler.web.service.RequestFormService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
public class RequestFormController {
    final RequestFormService requestFormService;

    public RequestFormController(RequestFormService requestFormService) {
        this.requestFormService = requestFormService;
    }

    @GetMapping("/requests")
    public List<RequestForm> getRequests(){
        return requestFormService.findAllRequests();
    }
    @GetMapping("/request/{id}")
    public Optional<RequestForm> getRequestById(@PathVariable("id") Long id){
        return requestFormService.findById(id);
    }
    @PostMapping("/saveRequest")
    public RequestForm saveEmployer (@Valid @RequestBody RequestForm requestForm){
        return requestFormService.saveRequestForm(requestForm);
    }
    @PutMapping("/updateRequest")
    public RequestForm updateEmployer(@RequestBody RequestForm requestForm) {
        return requestFormService.updateRequestForm(requestForm);
    }
    @DeleteMapping("/deleteRequest")
    public void deleteEmployer(@Valid @RequestBody RequestForm requestForm){
        requestFormService.deleteRequestForm(requestForm);
    }
}
