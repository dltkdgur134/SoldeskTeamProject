package com.soldesk6F.ondal.user.service;


import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class EmailService {

    @Value("${mailgun.api.key}")
    private String apiKey;

    @Value("${mailgun.domain}")
    private String domain;

    private final RestTemplate restTemplate = new RestTemplate();
    
    public void sendVerificationMail(String to, String link) {
        String url = "https://api.mailgun.net/v3/" + domain + "/messages";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", apiKey);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String htmlContent = "<div style='font-family: Arial, sans-serif;'>" +
                             "<h2 style='color: #333;'>[온달]</h2>" +
                             "<p>이메일 인증을 위해 아래 버튼을 눌러주세요:</p>" +
                             "<a href='" + link + "' style='display: inline-block; margin-top: 10px; padding: 10px 20px; background-color: #ffc107; color: white; text-decoration: none; border-radius: 5px;'>이메일 인증하기</a>" +
                             "<p style='margin-top: 20px; font-size: 12px; color: #aaa;'>본 메일은 자동 발송되었습니다.</p>" +
                             "</div>";

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("from", "온달 서비스 <noreply@" + domain + ">");
        form.add("to", to);
        form.add("subject", "이메일 인증을 완료해주세요");
        form.add("html", htmlContent); // <-- 요기!

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(form, headers);
        restTemplate.postForEntity(url, request, String.class);
    }
    
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }    
    
}