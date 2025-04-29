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
    public void sendMessage(Long chatRoomId, Long senderId, Long receiverId, String chatPairKey, String content, String messageType) {
        //String chatPairKey = generateChatPairKey(senderId, receiverId);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setChatRoomId(chatRoomId);
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessageContent(content);
        chatMessage.setIsRead(0);
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessage.setChatPairKey(chatPairKey);
        chatMessage.setMessageType(messageType);

        chatMessageMapper.saveMessage(chatMessage);
    }

    private String generateChatPairKey(Long senderId, Long receiverId) {
        Long low = Math.min(senderId, receiverId);
        Long high = Math.max(senderId, receiverId);
        return low + "_" + high;
    }

    // 특정 사용자에게 온 메시지 조회
    public List<ChatMessage> getReceivedMessages(Long chatRoomId) {
        return chatMessageMapper.findMessagesByReceiver(chatRoomId);
    }

    public List<ChatMessage> getLatestMessagesByChatPair(Long chatRoomId, String messageType) {
        return chatMessageMapper.findLatestMessagesByChatRoomId(chatRoomId, messageType);
    }

    // 특정 사용자가 보낸 메시지 조회
    public List<ChatMessage> getSentMessages(Long senderId) {
        return chatMessageMapper.findMessagesBySender(senderId);
    }

    // 메시지를 읽음 처리
    public void markMessageAsRead(Long messageId) {
        chatMessageMapper.markMessageAsRead(messageId);
    }

    public List<ChatMessage> getChatDetail(Long chatRoomId, String chatPairKey,  String messageType) {
        return chatMessageMapper.findChatDetails(chatRoomId, chatPairKey, messageType);
    }

    public void markMessagesAsRead(Long chatRoomId, Long senderId, Long receiverId) {
        chatMessageMapper.markMessagesAsRead(chatRoomId, senderId, receiverId);
    }
}

