package com.example.worker;

import com.example.common_libs.feigns.ParticipantFeign;
import com.example.common_libs.model.Document;
import com.example.common_libs.model.Participant;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.camunda.bpm.client.task.ExternalTaskHandler;
import org.camunda.bpm.client.task.ExternalTaskService;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;
import org.camunda.spin.Spin;
import org.camunda.spin.json.SpinJsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Component
@ExternalTaskSubscription(topicName="doc-topic")
public class SendDocuments implements ExternalTaskHandler {

    @Autowired()
    private ParticipantFeign participantFeign;

    @Override
    public void execute(ExternalTask externalTask, ExternalTaskService externalTaskService) {

        String myName = externalTask.getVariable("myParticipant");
        Participant myParticipant = participantFeign.getByName(myName).get(0);
        Boolean isHarvard = externalTask.getVariable("isHarvard");
        String uniName = "undefined";

        List<String>docs = new ArrayList<>();
        if(isHarvard){
            for (Document d:
                    myParticipant.getDocuments()) {
                docs.add(d.getHarvardDoc());
            }
            uniName = "Harvard";
        }else{
            for (Document d:
                    myParticipant.getDocuments()) {
                docs.add(d.getColumbiaDoc());
            }
            uniName = "Columbia";
        }


        List<String> jsonDocs = new ArrayList<>();
        SpinJsonNode spinJsonNode = Spin.JSON(docs);
        spinJsonNode.elements().forEach(elem ->{
            jsonDocs.add(elem.stringValue());
        });

        Map<String, Object> variables = new HashMap<>();
        variables.put("docs", jsonDocs);
        variables.put("uniName", uniName);

        externalTaskService.complete(externalTask, variables);

    }
}