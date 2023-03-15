package com.mustafafindik.artemisapp.service;

import com.mustafafindik.artemisapp.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class ReceiverService {
    Logger logger = LoggerFactory.getLogger(ReceiverService.class);
    @JmsListener(destination = "${jms.queue}")
    public void receiverMessage(String message){
        logger.info("Received message is :" +message);
    }
    @JmsListener(destination = "${jms.queue}")
    public void receiverObject(Message objmessage){
        logger.info("Received object message is : " + objmessage);
    }
}
