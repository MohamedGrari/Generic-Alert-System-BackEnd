package com.jobSchedule.JobScheduler.Quartz;

import com.jobSchedule.JobScheduler.web.Entity.AttributeConfiguration;
import com.jobSchedule.JobScheduler.web.Entity.Employer;
import com.jobSchedule.JobScheduler.web.Service.AttributeConfigurationService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@Component
public class EntityListener {
    private static LocalDate oldEndContract;
//    private static String oldPosition;
//    private static String oldStatus;
//    private static String oldContractType;
    //TODO
    private static Field[] allFields;

    private AttributeConfigurationService attributeConfigurationService;

    @Autowired
    public EntityListener(AttributeConfigurationService attributeConfigurationService) {
        this.attributeConfigurationService = attributeConfigurationService;
    }
    public static List<AttributeConfiguration> attributeConfigurations = new ArrayList<>();
    public static Field[] getAllFields(){ return allFields;}
    public static List<String> attributes = new ArrayList<>();
    public static List<String> getAttributes(){ return attributes;}
    public static List<Object> oldAttributeValues = new ArrayList<>();
    @PostConstruct
    public void onConstruct(){
        attributeConfigurations = attributeConfigurationService.findAllAttributeConfiguration();
    }

    public static List<Object> getOldAttributes(){
        return oldAttributeValues;
    }
    public static LocalDate getOldEndContract() {
        return oldEndContract;
    }
//    public static String getOldPosition() {
//        return oldPosition;
//    }
//    public static String getOldStatus() {
//        return oldStatus;
//    }
//    public static String getOldContractType() {
//        return oldContractType;
//    }

    public Object invokeGetter(Object obj, String variableName)
    {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(variableName, obj.getClass());
            Method getter = pd.getReadMethod();
            Object f = getter.invoke(obj);
            System.out.println(f);
            return f;
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException |
                 IntrospectionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostLoad
    public void onLoad(Employer employer) throws IllegalAccessException {
        //TODO
        for (AttributeConfiguration attributeConfiguration : attributeConfigurations){
            if(Objects.equals(attributeConfiguration.getAttributeType(), "String"))
                attributes.add(attributeConfiguration.getAttributeName());
        }
        System.out.println("attributes = " + attributes);
        for (String attribut :attributes) {
            oldAttributeValues.add(invokeGetter(employer, attribut));
        }
        System.out.println("oldAttributeValues = " + oldAttributeValues);

//         allFields = Employer.class.getDeclaredFields();
//        System.out.println("allFields = " + Arrays.toString(allFields));
//        List<Object> privateFields = new ArrayList<>();
//        for (Field field : allFields) {
//            field.setAccessible(true);
//            privateFields.add(field.get(employer));
//        }
//        System.out.println("privateFields = " + privateFields);


//        oldPosition = employer.getPosition();
//        oldStatus = employer.getStatus();
//        oldContractType = employer.getContractType();
//        oldEndContract = employer.getEndContract();
    }
    @PostPersist
    public void notifySubForPersist(Employer employer){
        EventHandler.subscribe(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handlePersisting(employer);
    }
    @PreUpdate
    public void notifySubForUpdate(Employer employer){
        EventHandler.updateSubscriber(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleUpdating(employer);
        eventHandler.handleEmployerUpdating(employer);
    }
    @PreRemove
    public void unsubscribe(Employer employer){
        EventHandler.unSubscribe(employer);
        EventHandler eventHandler = ApplicationContextHolder.getContext().getBean(EventHandler.class);
        eventHandler.handleEmployerDeleting(employer);
    }
}