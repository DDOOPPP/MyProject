package com.gi.demo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PingController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/send")
    @SendTo("/topic/public")
    public String send(@Payload String message) {
        log.info(message);
        return "Accepted"; // 문자열 그대로 broadcast
    }

    @MessageMapping("/chat.dm")
    @SendTo("/queue/dm")
    public void dm(@Payload  String message) {
        messagingTemplate.convertAndSendToUser(
                "testuser",
                "/queue/dm",
                message
        );
        log.info("testuser: {}",message);
    }
}