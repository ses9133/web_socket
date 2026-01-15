package org.demo.web_socket_step.sse.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.demo.web_socket_step.polling.chat.Chat;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseChatService {
    private final SseChatRepository sseChatRepository;

    // 접속한 클라이언트들을 보관
    // TODO - ConcurrentHashMap
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 1. SSE 연결 - 클라이언트가 접속하면 호출됨
    public SseEmitter createConnection(String clientId) {
        // 1분간 연결 유지 (시간은 설정하기 나름)
        SseEmitter emitter = new SseEmitter(60 * 1000L);

        // 새로운 연결 요청자의 정보를 서버단 메모리에서 관리해야함
        emitterMap.put(clientId, emitter);
        log.info("SSE 연결 추가: {}", clientId);

        // 연결 종료 / 타임아웃 시 목록에서 제거하는 콜백
        emitter.onCompletion(() -> emitterMap.remove(clientId)); // 연결 종료시 제거
        emitter.onTimeout(() -> emitterMap.remove(clientId)); // 타임아웃 시 제거
        emitter.onError((e) -> emitterMap.remove(clientId));

        // 클라이언트 측으로 메시지 발송
        try {
            emitter.send(SseEmitter.event().name("connect").data("연결됨"));
        } catch (IOException e) {
            log.error("초기 전송 실패: ", e);
        }

        return emitter;
    }

    // 2. 클라이언트에서 온 메시지를 DB에 저장하고 그 메시지를 구독자들에게 전파(브로드 캐스트)
    public void addMessage(String message, String sender) {
        Chat chat = Chat.builder()
                .message(message)
                .sender(sender)
                .build();

        sseChatRepository.save(chat);

        // 접속한 모든 사람들에게 알림(브로드 캐스트)
        // 데이터를 저장하자마자 바로 뿌림
        broadcast(message, sender);
    }

    // 실제 알림 보내기 - 브로드 캐스트
    public void broadcast(String message, String sender) {
        // 간단하게 문자열로 조합해서 보냄 (JSON 형식 아님)
        String formattedMessage = sender + ":" + message;
        // js 측에서 홍길동:만나서 반가워 ==> : 기준으로 보낸이 추출, 메시지 추출

         emitterMap.forEach((id, emitter) -> {
             try {
                 emitter.send(SseEmitter.event().name("newMessage").data(formattedMessage)); // 클라이언트 이벤트 명: newMessage
             } catch (IOException e) {
                 emitterMap.remove(id); // 전송 실패시 제거
             }
         });
    }

    // 전체 목록 조회
    public List<Chat> findAll() {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return sseChatRepository.findAll(sort);
    }
}
