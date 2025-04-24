package one.dfy.bily.api.common.mapper;

import one.dfy.bily.api.common.dto.ChatMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ChatMessageMapper {

    @Insert("""
    INSERT INTO TBL_CHAT_MESSAGE (
        SENDER_ID, RECEIVER_ID, MESSAGE_CONTENT,
        IS_READ, SENT_AT, CHAT_ROOM_ID, CHAT_PAIR_KEY
    ) VALUES (
        #{senderId}, #{receiverId}, #{messageContent},
        #{isRead}, #{sentAt}, #{chatRoomId}, #{chatPairKey}
    )
""")
    void saveMessage(ChatMessage chatMessage);

    @Select("""
    SELECT m.*, sender.USER_NAME AS senderName, receiver.USER_NAME AS receiverName
    FROM TBL_CHAT_MESSAGE m
    JOIN TBL_USER sender ON m.SENDER_ID = sender.USER_ID
    JOIN TBL_USER receiver ON m.RECEIVER_ID = receiver.USER_ID
    JOIN (
        SELECT CHAT_PAIR_KEY, MAX(SENT_AT) AS maxSentAt
        FROM TBL_CHAT_MESSAGE
        WHERE CHAT_ROOM_ID = #{chatRoomId}
        GROUP BY CHAT_PAIR_KEY
    ) latest
      ON m.CHAT_PAIR_KEY = latest.CHAT_PAIR_KEY
     AND m.SENT_AT = latest.maxSentAt
    WHERE m.CHAT_ROOM_ID = #{chatRoomId}
    ORDER BY m.SENT_AT DESC;
""")
    @Results({
            @Result(property = "id", column = "ID"),
            @Result(property = "senderId", column = "SENDER_ID"),
            @Result(property = "receiverId", column = "RECEIVER_ID"),
            @Result(property = "messageContent", column = "MESSAGE_CONTENT"),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "sentAt", column = "SENT_AT"),
            @Result(property = "chatPairKey", column = "CHAT_PAIR_KEY"),
            @Result(property = "chatRoomId", column = "CHAT_ROOM_ID"),
            @Result(property = "senderName", column = "senderName"),  // 🔹 보낸 사람 이름 추가
            @Result(property = "receiverName", column = "receiverName")  // 🔹 받는 사람 이름 추가 (필요하면 사용)

    })
    List<ChatMessage> findMessagesByReceiver(@Param("chatRoomId") Long chatRoomId);

    @Select("""
    SELECT c.*,
           sender.USER_NAME AS senderName,
           receiver.USER_NAME AS receiverName
    FROM TBL_CHAT_MESSAGE c
    JOIN TBL_USER sender ON c.SENDER_ID = sender.USER_ID
    JOIN TBL_USER receiver ON c.RECEIVER_ID = receiver.USER_ID
    INNER JOIN (
        SELECT CHAT_PAIR_KEY, MAX(SENT_AT) AS maxSentAt
        FROM TBL_CHAT_MESSAGE
        WHERE CHAT_ROOM_ID = #{chatRoomId}
        GROUP BY CHAT_PAIR_KEY
    ) latest 
    ON c.CHAT_PAIR_KEY = latest.CHAT_PAIR_KEY 
       AND c.SENT_AT = latest.maxSentAt
    WHERE c.CHAT_ROOM_ID = #{chatRoomId}
    ORDER BY c.SENT_AT DESC
""")
    @Results({
            @Result(property = "id", column = "MESSAGE_ID"),
            @Result(property = "senderId", column = "SENDER_ID"),
            @Result(property = "receiverId", column = "RECEIVER_ID"),
            @Result(property = "messageContent", column = "MESSAGE_CONTENT"),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "sentAt", column = "SENT_AT"),
            @Result(property = "chatRoomId", column = "CHAT_ROOM_ID"),
            @Result(property = "chatPairKey", column = "CHAT_PAIR_KEY"),
            @Result(property = "senderName", column = "senderName"),
            @Result(property = "receiverName", column = "receiverName")
    })
    List<ChatMessage> findLatestMessagesByChatRoomId(@Param("chatRoomId") Long chatRoomId);




    @Select("SELECT * FROM TBL_CHAT_MESSAGE WHERE SENDER_ID = #{senderId} ORDER BY SENT_AT DESC")
    List<ChatMessage> findMessagesBySender(@Param("senderId") Long senderId);

    @Update("UPDATE TBL_CHAT_MESSAGE SET IS_READ = 1 WHERE MESSAGE_ID = #{messageId}")
    void markMessageAsRead(@Param("messageId") Long messageId);

    // 대화 상세 (chatRoomId, senderId)
    @Select("""
    SELECT
        c.MESSAGE_ID,
        c.SENDER_ID,
        c.RECEIVER_ID,
        c.MESSAGE_CONTENT,
        c.IS_READ,
        c.SENT_AT,
        c.CHAT_ROOM_ID,
        sender.USER_NAME AS senderName,
        receiver.USER_NAME AS receiverName
    FROM TBL_CHAT_MESSAGE c
    JOIN TBL_USER sender ON c.SENDER_ID = sender.USER_ID
    JOIN TBL_USER receiver ON c.RECEIVER_ID = receiver.USER_ID
    WHERE c.CHAT_ROOM_ID = #{chatRoomId}
      AND c.CHAT_PAIR_KEY = #{chatPairKey}
    ORDER BY c.SENT_AT ASC
""")
    @Results({
            @Result(property = "id", column = "MESSAGE_ID"),
            @Result(property = "senderId", column = "SENDER_ID"),
            @Result(property = "receiverId", column = "RECEIVER_ID"),
            @Result(property = "messageContent", column = "MESSAGE_CONTENT"),
            @Result(property = "isRead", column = "IS_READ"),
            @Result(property = "sentAt", column = "SENT_AT"),
            @Result(property = "chatRoomId", column = "CHAT_ROOM_ID"),
            @Result(property = "senderName", column = "senderName"),
            @Result(property = "receiverName", column = "receiverName")
    })
    List<ChatMessage> findChatDetails(@Param("chatRoomId") Long chatRoomId,
                                      @Param("chatPairKey") String chatPairKey);


    // 읽음 처리 (chatRoomId + senderId → receiverId)
    @Update("UPDATE TBL_CHAT_MESSAGE SET IS_READ = 1 " +
            "WHERE CHAT_ROOM_ID = #{chatRoomId} AND SENDER_ID = #{senderId} AND RECEIVER_ID = #{receiverId} AND IS_READ = 0")
    void markMessagesAsRead(@Param("chatRoomId") Long chatRoomId,
                            @Param("senderId") Long senderId,
                            @Param("receiverId") Long receiverId);
}

