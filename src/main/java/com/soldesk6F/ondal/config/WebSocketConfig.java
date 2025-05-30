
package com.soldesk6F.ondal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	// 메세지 브로커 구성
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// 클라이언트가 구독(subscribe)할 prefix
		registry.enableSimpleBroker("/topic", "/queue");
		// 서버 측에서 처리할 prefix
		registry.setApplicationDestinationPrefixes("/app");
	}

	// STOMP 엔드포인트 설정 (SockJS 사용)
	@Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // /stomp 라는 엔드포인트로 WebSocket 또는 SockJS 연결
        registry.addEndpoint("/stomp")
        .setAllowedOriginPatterns("*")
        .setHandshakeHandler(new CustomHandshakeHandler())
        .addInterceptors(new HttpSessionHandshakeInterceptor(), new AuthHandshakeInterceptor())
        .withSockJS();
    }

}
