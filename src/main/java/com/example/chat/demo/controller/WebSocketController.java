package com.example.chat.demo.controller;

import com.example.chat.demo.model.ChatMessage;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.net.MalformedURLException;
import java.util.UUID;

@Controller
public class WebSocketController {

    private SimpMessagingTemplate messaging;

    public WebSocketController(final SimpMessagingTemplate messaging) {
        this.messaging = messaging;
    }

    // doesn't use -> old code delete here something from develop
    // doesn't use -> old code
    //    @MessageMapping("/chat.sendMessage")
//    @SendTo("/topic/publicChatRoom")
//    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
//        System.out.println("Message from: " + chatMessage.getSender().toUpperCase() + " >>> " + chatMessage.getContent());
//        return chatMessage;
//    }

    @MessageMapping("/{matchUuid}/chat/{teamUuid}/send")
    public void sendMessage(@DestinationVariable UUID matchUuid,
                                   @DestinationVariable UUID teamUuid, String message) throws MalformedURLException {
        System.out.println("MathcUuid Destination: " + matchUuid);
        System.out.println("TeamUuid Destination: " + teamUuid);
        System.out.println("Message: " + message);
        messaging.convertAndSend("/topic/" + matchUuid + "/chat/" + teamUuid, message);
    }

    @SubscribeMapping("/topic/{matchUuid}/chat/{teamUuid}")
    public void sendMessageHistory(@DestinationVariable UUID matchUuid,
                                   @DestinationVariable UUID teamUuid) {
        messaging.convertAndSend("/topic/" + matchUuid + "/chat/" + teamUuid,
                "{\"sender\":\"History\",\"content\":\"Message History Here Boy!\",\"type\":\"CHAT\"}");
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/publicChatRoom")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
