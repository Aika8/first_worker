package com.example.worker;


import com.example.common_libs.feigns.ParticipantFeign;
import com.example.common_libs.model.Document;
import com.example.common_libs.model.Participant;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.spin.Spin;
import lombok.extern.slf4j.Slf4j;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ExternalTaskSubscription(topicName = "perform-topic")
@Slf4j
public class GoToLawSchool implements ExternalTaskHandler {


    @Autowired()
    private ParticipantFeign participantFeign;


    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

            String name = externalTask.getVariable("name");
            int age = externalTask.getVariable("age");


        if (!(age < 50 && age > 18)) {
            externalTaskService.handleBpmnError(externalTask, "age_limit");
            return;
        }

        log.info("name :" + name + ",age :" + age);

        Participant myParticipant = new Participant();
        myParticipant.setName(name);
        myParticipant.setAge(age);
        myParticipant = participantFeign.save(myParticipant);
        log.info("participant saved: " + myParticipant);

        Document document = new Document();
        document.setHarvardDoc("harvard doc");
        document.setColumbiaDoc("columbia doc");
        document.setParticipant(myParticipant);
        participantFeign.saveDoc(document);
        log.info("document saved: " + document);

        List<String> participants = new ArrayList<>();

        for (Participant p : participantFeign.get()) {
            participants.add(p.getName());
        }

        List<String> pList = new ArrayList<>();
        SpinJsonNode spinJsonNode = Spin.JSON(participants);
        spinJsonNode.elements().forEach(elem -> {
            pList.add(elem.stringValue());
        });


        Map<String, Object> variables = new HashMap<>();
        variables.put("myParticipant", myParticipant.getName());
        variables.put("participants", pList);

        externalTaskService.complete(externalTask, variables);
    }
}
