package com.example.worker;

import com.example.common_libs.feigns.ParticipantFeign;
import com.example.common_libs.model.Participant;
import com.example.common_libs.model.Student;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExternalTaskSubscription(topicName="student-topic")
@Slf4j
public class BecomeStudent implements ExternalTaskHandler {

    @Autowired()
    private ParticipantFeign participantFeign;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        String myName = externalTask.getVariable("myParticipant");
        Participant myParticipant = participantFeign.getByName(myName).get(0);

        Student student = new Student(myParticipant.getName(), myParticipant.getAge());

        participantFeign.save(student);
        log.info("student saved");

        externalTaskService.complete(externalTask);

    }
}
