package com.soldesk6F.ondal.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;

public class TomcatConfig {

    @Bean
    public ServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        
        // HTTPS 기본 포트 (예: 8443)
        factory.setPort(8443); 
        
        // 추가로 HTTP 포트 열기
        factory.addAdditionalTomcatConnectors(createHttpConnector());
        return factory;
    }

    private Connector createHttpConnector() {
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(8080); // Kakao가 보낼 포트
        connector.setSecure(false);
        connector.setRedirectPort(8443); // 선택: HTTPS로 리디렉션할 수도 있음
        return connector;
    }
}