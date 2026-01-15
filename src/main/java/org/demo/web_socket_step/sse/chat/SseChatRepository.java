package org.demo.web_socket_step.sse.chat;

import org.demo.web_socket_step.polling.chat.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SseChatRepository extends JpaRepository<Chat, Long>{
}
