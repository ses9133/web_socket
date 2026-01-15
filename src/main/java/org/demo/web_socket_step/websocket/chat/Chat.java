package org.demo.web_socket_step.websocket.chat;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "chat_stomp_tb")
@Entity(name = "StompChat")
public class Chat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    @Column(nullable = false)
    private String message;

    @Builder
    public Chat(String sender, String message) {
        this.sender = sender;
        this.message = message;
    }
}
