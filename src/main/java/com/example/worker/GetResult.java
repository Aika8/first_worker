package com.example.worker;


import com.example.common_libs.feigns.ParticipantFeign;
import com.example.common_libs.model.Participant;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ExternalTaskSubscription(topicName="check-topic")
public class GetResult implements ExternalTaskHandler {

    @Autowired()
    private ParticipantFeign participantFeign;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        String myName = externalTask.getVariable("myParticipant");
        Participant myParticipant = participantFeign.getByName(myName).get(0);

        myParticipant = participantFeign.getByName(myParticipant.getName()).get(0);

        boolean isPass = false;
        if(myParticipant.getLASTScore() > 30){
            isPass = true;
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("myParticipant", myParticipant.getName());
        variables.put("isPass", isPass);
        variables.put("score", myParticipant.getLASTScore());

        externalTaskService.complete(externalTask, variables);

    }
}
