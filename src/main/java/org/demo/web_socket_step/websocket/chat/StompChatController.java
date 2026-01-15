package org.demo.web_socket_step.websocket.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class StompChatController {
    private final StompChatService stompChatService;

    // 1. 채팅방 화면(기존 글 목록 포함)
    @GetMapping("/stomp/chat")
    public String index(Model model) {
        model.addAttribute("chatList", stompChatService.findAll());
        return "stomp/index";
    }

    /**
     * 2. 메시지 수신(Publish)
     * - 클라이언트가 /pub/chat/message 로 메시지를 보내면 이 메서드가 실행됨
     * - @MessageMapping: 웹 소켓 메시지 라우팅 (HTTP @RequestMapping 과 비슷)
     */
    @MessageMapping("/chat/message")
    public void receiveMessage(Map<String, String> payload) {
        // DTO 대신 Map 을 사용하여 JSON 데이터 받는 형식으로 만들거임.
        String message = payload.get("message");
        String sender = payload.get("sender");

        // DB 에 저장하고 뿌림(방송함)
        stompChatService.saveAndBroadcast(message, sender);
    }
}
