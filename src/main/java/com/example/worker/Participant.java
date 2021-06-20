package com.example.worker;

import com.example.common_libs.feigns.PersonFeign;
import com.example.common_libs.model.Person;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@ExternalTaskSubscription(topicName="person-topic")
public class Participant implements ExternalTaskHandler {


    @Autowired()
    private PersonFeign personFeign;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        String name = externalTask.getVariable("name");

        Person person = personFeign.getByName(name).get(0);

        System.out.println(person);

        Map<String, Object> variables = new HashMap<>();
        variables.put("participant",person);

        externalTaskService.complete(externalTask, variables);

    }
}
