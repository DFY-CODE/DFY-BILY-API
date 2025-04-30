package one.dfy.bily.api.chat.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.dfy.bily.api.chat.dto.ChatMessage;
import one.dfy.bily.api.chat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "채팅 메시지 API", description = "1:1 채팅 메시지 처리 API")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    @Operation(summary = "메시지 전송", description = "사용자가 채팅방에서 메시지를 전송합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ChatMessage.class),
                    examples = @ExampleObject(value = "{\n  \"chatRoomId\": 101,\n  \"chatPairKey\": \"101_2001\",\n  \"senderId\": 1001,\n  \"receiverId\": 2001,\n  \"messageContent\": \"안녕하세요, 문의드립니다.\",\n  \"messageType\": \"I\"\n}")
            )
    )
    @ApiResponse(responseCode = "200", description = "메시지 전송 완료")
    @PostMapping("/send")
    public void sendMessage(@RequestBody ChatMessage chatMessage) {
        chatMessageService.sendMessage(
                chatMessage.getChatRoomId(),
                chatMessage.getSenderId(),
                chatMessage.getReceiverId(),
                chatMessage.getChatPairKey(),
                chatMessage.getMessageContent(),
                chatMessage.getMessageType()
        );
    }

    @Operation(summary = "받은 메시지 조회", description = "채팅방 ID로 받은 메시지 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ChatMessage.class),
                    examples = @ExampleObject(value = "[{\n  \"id\": 1,\n  \"chatRoomId\": 101,\n  \"chatPairKey\": \"101_2001\",\n  \"senderId\": 2001,\n  \"receiverId\": 1001,\n  \"messageContent\": \"문의 답변드립니다.\",\n  \"messageType\": \"I\",\n  \"isRead\": 0,\n  \"senderName\": \"관리자\",\n  \"receiverName\": \"홍길동\",\n  \"sentAt\": \"2025-04-24 10:00:00\"\n}]"))
    )
    @GetMapping("/received")
    public List<ChatMessage> getReceivedMessages(@RequestParam Long chatRoomId) {
        return chatMessageService.getReceivedMessages(chatRoomId);
    }

    @Operation(summary = "보낸 메시지 조회", description = "보낸 사람 ID로 보낸 메시지 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/sent/{senderId}")
    public List<ChatMessage> getSentMessages(@PathVariable Long senderId) {
        return chatMessageService.getSentMessages(senderId);
    }

    @Operation(summary = "메시지 읽음 처리", description = "메시지 ID로 읽음 처리합니다.")
    @ApiResponse(responseCode = "200", description = "읽음 처리 완료")
    @PutMapping("/read/{messageId}")
    public void markMessageAsRead(@PathVariable Long messageId) {
        chatMessageService.markMessageAsRead(messageId);
    }

    @Operation(summary = "최신 메시지 조회", description = "chatRoomId, messageType으로 최신 메시지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/latest")
    public List<ChatMessage> getLatestMessages(
            @RequestParam Long chatRoomId,
            @RequestParam String messageType) {
        return chatMessageService.getLatestMessagesByChatPair(chatRoomId, messageType);
    }

    @Operation(summary = "대화 상세 조회", description = "특정 사용자와의 전체 메시지를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/detail")
    public List<ChatMessage> getChatDetail(
            @RequestParam Long chatRoomId,
            @RequestParam(required = false) String chatPairKey,
            @RequestParam(required = false) String messageType) {
        return chatMessageService.getChatDetail(chatRoomId, chatPairKey, messageType);
    }

    @Operation(summary = "읽음 처리 (대화 전체)", description = "chatRoomId와 senderId, receiverId 기준으로 메시지를 일괄 읽음 처리합니다.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "object"),
                    examples = @ExampleObject(value = "{\n  \"chatRoomId\": 101,\n  \"senderId\": 1001,\n  \"receiverId\": 2001\n}")
            )
    )
    @ApiResponse(responseCode = "200", description = "읽음 처리 완료")
    @PostMapping("/read")
    public void markMessagesAsRead(@RequestBody Map<String, Object> payload) {
        Long chatRoomId = Long.valueOf(payload.get("chatRoomId").toString());
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());

        chatMessageService.markMessagesAsRead(chatRoomId, senderId, receiverId);
    }
}
