package com.example.worker;

import com.example.common_libs.feigns.ParticipantFeign;
import com.example.common_libs.model.Participant;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Random;


@Component
@ExternalTaskSubscription(topicName="grade-topic")
public class Grade implements ExternalTaskHandler {

    @Autowired()
    private ParticipantFeign participantFeign;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        String name = externalTask.getVariable("participant");

        int score = new Random().nextInt(100 - 15) + 15;

        Participant participant = participantFeign.getByName(name).get(0);
        participant.setLASTScore(score);

        participantFeign.save(participant);

        externalTaskService.complete(externalTask);

    }
}
