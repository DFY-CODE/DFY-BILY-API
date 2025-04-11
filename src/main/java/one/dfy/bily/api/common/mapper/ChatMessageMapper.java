package one.dfy.bily.api.common.mapper;

import one.dfy.bily.api.common.dto.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO TBL_CHAT_MESSAGE (SENDER_ID, RECEIVER_ID, MESSAGE_CONTENT, IS_READ, SENT_AT, CHAT_ROOM_ID) " +
            "VALUES (#{senderId}, #{receiverId}, #{messageContent}, FALSE, #{sentAt} ,#{chatRoomId})")
    void saveMessage(ChatMessage chatMessage);

    @Select("""
    SELECT 
        c.*, 
        sender.USER_NAME AS senderName,  
        receiver.USER_NAME AS receiverName  
    FROM TBL_CHAT_MESSAGE c
    JOIN TBL_USER sender ON c.SENDER_ID = sender.USER_ID
    JOIN TBL_USER receiver ON c.RECEIVER_ID = receiver.USER_ID
    WHERE c.CHAT_ROOM_ID = #{chatRoomId}
    ORDER BY c.SENT_AT DESC
""")
    @Results({
            @Result(property = "id", column = "ID"),
            @Result(property = "senderId", column = "SENDER_ID"),
            @Result(property = "receiverId", column = "RECEIVER_ID"),
            @Result(property = "messageContent", column = "MESSAGE_CONTENT"),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "sentAt", column = "SENT_AT"),
            @Result(property = "chatRoomId", column = "CHAT_ROOM_ID"),
            @Result(property = "senderName", column = "senderName"),  // 🔹 보낸 사람 이름 추가
            @Result(property = "receiverName", column = "receiverName")  // 🔹 받는 사람 이름 추가 (필요하면 사용)
    })
    List<ChatMessage> findMessagesByReceiver(@Param("chatRoomId") Long chatRoomId);


    @Select("SELECT * FROM TBL_CHAT_MESSAGE WHERE SENDER_ID = #{senderId} ORDER BY SENT_AT DESC")
    List<ChatMessage> findMessagesBySender(@Param("senderId") Long senderId);

    @Update("UPDATE TBL_CHAT_MESSAGE SET IS_READ = TRUE WHERE MESSAGE_ID = #{messageId}")
    void markMessageAsRead(@Param("messageId") Long messageId);
}

