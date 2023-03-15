package com.mustafafindik.artemisapp.controller;

import com.mustafafindik.artemisapp.service.DispatcherService;
import com.mustafafindik.artemisapp.service.ReceiverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
public class MessageController {

    private final DispatcherService dispatcherService;

    public MessageController(DispatcherService dispatcherService) {
        this.dispatcherService = dispatcherService;
    }
    @PostMapping
    public ResponseEntity send(@RequestBody String message){
        dispatcherService.sendTheMessage(message);
        return ResponseEntity.ok("Message send : " + message);
    }
}
