package one.dfy.bily.api.common.controller;

import one.dfy.bily.api.common.dto.ChatMessage;
import one.dfy.bily.api.common.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    // 메시지 전송
    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage chatMessage) {
        Long adminId = 1L; // 관리자의 ID (예제)
        chatMessageService.sendMessage(chatMessage.getChatRoomId(), chatMessage.getSenderId(), chatMessage.getReceiverId(), chatMessage.getChatPairKey(), chatMessage.getMessageContent());
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

    // 최신 메시지 리스트용 api
    @GetMapping("/latest")
    public List<ChatMessage> getLatestMessages(
            @RequestParam Long chatRoomId) {

        return chatMessageService.getLatestMessagesByChatPair(chatRoomId);
    }

    // ✅ 특정 사용자와의 대화 상세 (전체 메시지) 조회
    @GetMapping("/detail")
    public List<ChatMessage> getChatDetail(
            @RequestParam Long chatRoomId,
            @RequestParam(value = "chatPairKey", required = false) String chatPairKey
    ) {
        return chatMessageService.getChatDetail(chatRoomId, chatPairKey);
    }

    // ✅ 읽음 처리 (특정 chatRoomId에서 특정 sender의 메시지 전체)
    @PostMapping("/read")
    public void markMessagesAsRead(@RequestBody Map<String, Object> payload) {
        Long chatRoomId = Long.valueOf(payload.get("chatRoomId").toString());
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());

        chatMessageService.markMessagesAsRead(chatRoomId, senderId, receiverId);
    }
}
