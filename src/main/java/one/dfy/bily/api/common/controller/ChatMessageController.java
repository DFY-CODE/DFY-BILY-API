package one.dfy.bily.api.common.controller;

import one.dfy.bily.api.common.dto.ChatMessage;
import one.dfy.bily.api.common.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    // 메시지 전송
    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage chatMessage) {
        Long adminId = 1L; // 관리자의 ID (예제)
        chatMessageService.sendMessage(chatMessage.getChatRoomId(), chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getMessageContent());
    }

    // 받은 메시지 목록 조회
    @GetMapping("/received")
    public List<ChatMessage> getReceivedMessages(
            @RequestParam Long chatRoomId) {
        return chatMessageService.getReceivedMessages(chatRoomId);
    }

    // 보낸 메시지 목록 조회
    @GetMapping("/sent/{senderId}")
    public List<ChatMessage> getSentMessages(@PathVariable Long senderId) {
        return chatMessageService.getSentMessages(senderId);
    }

    // 메시지를 읽음 처리
    @PutMapping("/read/{messageId}")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatMessageService.markMessageAsRead(messageId);
    }
}
