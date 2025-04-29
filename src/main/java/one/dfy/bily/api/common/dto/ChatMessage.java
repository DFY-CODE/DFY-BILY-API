package one.dfy.bily.api.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long chatRoomId; // 공간 ID
    private String chatPairKey; //chatRoomId + receiverId 조합 그룹화
    private Long senderId;
    private Long receiverId;
    private String messageContent;
    private int isRead;
    private String senderName; // 🔹 보낸 사람 이름
    private String receiverName; // 🔹 받는 사람 이름
    private String messageType; //  메시지 타입 코드 (I:문의, R:예약, S:공간관리)

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime sentAt;
}



