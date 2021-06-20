package com.example.worker;


import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ExternalTaskSubscription(topicName="perform-topic")
public class GoToBallet implements ExternalTaskHandler {
    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {
        int weight = externalTask.getVariable("weight");
        String examResult = "Undefined";
        boolean isPass = false;

        if (weight > 50) {
            externalTaskService.handleBpmnError(externalTask, "too fat!");
        }

        int score = externalTask.getVariable("exam_score");


        if (score > 70) {
            isPass = true;
            examResult = "intermediate group";
        } else if (score > 50) {
            isPass = true;
            examResult = "beginner group";
        } else {
            isPass = false;
            examResult = "you failed";
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("examResult", examResult);
        variables.put("isPass", isPass);

        externalTaskService.complete(externalTask, variables);
    }
}
