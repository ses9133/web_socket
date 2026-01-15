package org.demo.web_socket_step.websocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StompChatService {
    private final StompChatRepository stompChatRepository;

    // 1. 스프링에서 제공하는 메세지 전용 도구
    // - 특정 경로 (/sub/...) 를 구독하고 있는 클라이언트에게 메세지를 푸시(발송)할 수 있음
    private final SimpMessagingTemplate simpMessagingTemplate;

    // 채팅 비즈니스 로직 --> 저장하고 뿌린다...
    public void saveAndBroadcast(String message, String sender) {
        Chat chat = Chat.builder()
                .message(message)
                .sender(sender)
                .build();

        // 1) 저장
        stompChatRepository.save(chat);

        // json이 아닌 우리가 형식을 sender:홍길동 이런식으로 만들었음
        String formattedMessage = sender + ":" + message;

        // 2) /sub/chat/room1 구독자들에게 formattedMessage 형식으로 뿌림
        simpMessagingTemplate.convertAndSend("/sub/chat/room1", formattedMessage);
    }

    // 전체 조회
    public List<Chat> findAll() {
        return stompChatRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }
}
