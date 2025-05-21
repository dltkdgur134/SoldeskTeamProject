package com.soldesk6F.ondal.chat;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.messaging.simp.stomp.*;

import org.springframework.web.socket.messaging.*;

@Component
public class StompDebugLogger {

	@EventListener
	public void handleConnect(SessionConnectEvent event) {
		System.out.println("🔌 WebSocket 연결됨!");
	}

	@EventListener
	public void handleInboundMessage(SessionSubscribeEvent event) {
		System.out.println("📥 구독 요청 도착!");
	}

	
}
