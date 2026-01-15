package org.demo.web_socket_step.websocket.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // STOMP 프로토콜 사용을 위한 어노테이션
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 1. 웹 소켓 연결을 위한 엔드 포인트 설정(handshake)
     * - 클라이언트가 서버와 최초 연결을 맺는 주소를 정의(실시간 통신을 위해)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /ws-stomp 엔드 포인트로 설정
        // CORS: 모든 도메인에서 접속 가능하도록 허용
        // Cross Origin Resource Sharing
        // SockJS 지원 허용: 웹 소켓을 지원하지 않는 브라우저에서도 HTTP Polling 등으로 비슷하게 동작하도록 허용
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    /**
     * 2. 메시지 브로커 설정 (Publish & Subscribe)
     * - 추가적으로 보통 Prefix 설정을 미리 해둠
     * - /sub
     * - /pub
     * ---> /sub/chat/room1
     * ---> /pub/chat/message
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. 클라이언트가 구독 경로를 할 수 있는 경로를 지정해줌
        // 예: /sub/chat/room1
        registry.enableSimpleBroker("/sub");

        // 2. 클라이언트가 서버측으로 메세지를 보낼 수 있는 경로를 지정해줌
        // 예: /pub/chat/message
        registry.setApplicationDestinationPrefixes("/pub");
    }
}

