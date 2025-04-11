package one.dfy.bily.api.common.service;

import one.dfy.bily.api.common.dto.ChatMessage;
import one.dfy.bily.api.common.mapper.ChatMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    // 메시지 저장
    public void sendMessage(Long chatRoomId, Long senderId, Long receiverId, String messageContent) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessageContent(messageContent);
        chatMessage.setIsRead(false);
        chatMessage.setSentAt(LocalDateTime.now()); // 현재 시간 설정

        chatMessageMapper.saveMessage(chatMessage);
    }

    // 특정 사용자에게 온 메시지 조회
    public List<ChatMessage> getReceivedMessages(Long chatRoomId) {
        return chatMessageMapper.findMessagesByReceiver(chatRoomId);
    }

    // 특정 사용자가 보낸 메시지 조회
    public List<ChatMessage> getSentMessages(Long senderId) {
        return chatMessageMapper.findMessagesBySender(senderId);
    }

    // 메시지를 읽음 처리
    public void markMessageAsRead(Long messageId) {
        chatMessageMapper.markMessageAsRead(messageId);
    }
}

