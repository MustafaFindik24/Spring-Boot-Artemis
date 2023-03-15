package com.mustafafindik.artemisapp.service;

import com.mustafafindik.artemisapp.entity.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class DispatcherService {
    private final JmsTemplate jmsTemplate; // Jms'ye mesaj göndermekten sorumlu
    public DispatcherService(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }
    @Value("${jms.queue}")
    String queue;//Mesajı göndereceğimiz bir destination queue
    @Value("Q.object")
    String objqueue;

    public void sendTheMessage(String message){
        jmsTemplate.convertAndSend(queue,message);
    }
    public void sendObject(Message objmessage){
        jmsTemplate.convertAndSend(objqueue,objmessage);
    }
}
