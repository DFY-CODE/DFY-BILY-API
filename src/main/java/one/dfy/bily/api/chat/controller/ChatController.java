package one.dfy.bily.api.chat.controller;

import one.dfy.bily.api.chat.dto.ChatMessage;
import one.dfy.bily.api.chat.service.ChatMessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private final ChatMessageService chatMessageService;

    @Autowired
    public ChatController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    // STOMP 메시지 수신 처리 (WebSocket)
    @MessageMapping("/chat.send") // 클라이언트에서 "/app/chat.send"로 publish
    @SendTo("/topic/messages")    // 구독중인 모든 클라이언트에게 전파
    public ChatMessage handleMessage(ChatMessage message) {
        // 현재 시간 설정
        message.setSentAt(LocalDateTime.now());

        // DB에 저장
        chatMessageService.sendMessage(
                message.getChatRoomId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getChatPairKey(),
                message.getMessageContent(),
                message.getMessageType()
        );

        return message; // 클라이언트에 broadcast
    }
}


