package org.demo.web_socket_step.sse.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RequiredArgsConstructor
@Controller
public class SseChatController {
    private final SseChatService sseChatService;

    // 1. 채팅방 화면 (기존 글 목록 + SSE 연결 JS 포함)
    @GetMapping("/sse/chat")
    public String index(Model model) {
        model.addAttribute("chatList", sseChatService.findAll());
        return "sse/index";
    }

    // 2. [SSE 연결] 여기 경로로오면 클라이언트가 이제 구독함
    // 중요! - 응답할때 HTTP 메시지 헤더에 이제부터 지속연결이야! 라고 명시함

    // HTTP/1.1 200 OK (응답메시지 시작줄)
    // Content-Type: text/event-stream;charset=UTF-8  // produces = MediaType.TEXT_EVENT_STREAM_VALUE) 선언함으로써 이런식으로 마임타입 정해서 응답 내려줌

    // produces: 나는 이런 종류의 데이터들을 생산한다.
    // produces = MediaType.TEXT_HTML_VALUE  : HTML 파일 형식
    // produces = MediaType.APPLICATION_JSON_VALUE  : 데이터(JSON 형식 문자열)
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter connect() {
        // SseEmitter 객체를 설정하고 준비해서 js 측으로 반환 처리
        // --> 서비스 단에서 처리
        return sseChatService.createConnection(UUID.randomUUID().toString());
    }

    @PostMapping("/sse/send")
    public String sendMessage(@RequestParam(name = "message") String message,
                              @RequestParam(name = "sender") String sender) {

        sseChatService.addMessage(message, sender);
        return "redirect:/sse/chat";
    }
}
