package com.soldesk6F.ondal.email;


import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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
    
    public String sendVerificationMailWithCid(String email) throws IOException {
        String code = generateCode();

        String url = "https://api.mailgun.net/v3/" + domain + "/messages";

        String htmlContent =
        	    "<!DOCTYPE html>" +
        	    "<html lang='ko'>" +
        	    "<head><meta charset='UTF-8'></head>" +
        	    "<body style='margin: 0; font-family: Arial, sans-serif; background: #fff9db;'>" +

        	    "  <div style='background: #ffe066; padding: 15px 20px; display: flex; align-items: center;'>" +
        	    "    <img src='https://i.imgur.com/ibxwQWU.png' alt='로고' style='height: 40px; margin-right: 12px;'>" +
        	    "    <span style='font-size: 20px; font-weight: bold;'>온달 온라인 배달</span>" +
        	    "  </div>" +
        	    "  <div style='padding: 30px; text-align: center;'>" +
        	    "    <p style='font-size: 18px;'>안녕하세요, 온달 서비스에 가입해 주셔서 감사합니다.</p>" +
        	    "    <p style='font-size: 16px; margin-top: 20px;'><strong>아래 인증 코드를 입력해 주세요:</strong></p>" +
        	    "    <div style='background: #fff3bf; font-size: 30px; padding: 12px 24px; display: inline-block; margin-top: 15px; border-radius: 8px; font-weight: bold; letter-spacing: 3px;'>" +
        	    code + "</div>" +
        	    "  </div>" +
        	    "  <div style='background: #fff; padding: 20px; text-align: center; font-size: 12px; color: #666; border-top: 1px solid #ddd;'>" +
        	    "    <p>(주) 평강공주의남자들<br>서울 강남구 봉은사로 119 성욱빌딩 6층</p>" +
        	    "    <p>사업자번호 : 123-45-67890 | TEL : 02-1234-5678 | FAX : 02-9999-8888</p>" +
        	    "  </div>" +
        	    "</body></html>";

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("api", apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("from","온달 서비스 <noreply@" + domain + ">");
        body.add("to", email);
        body.add("subject", "온달 이메일 인증");
        body.add("html", htmlContent); 




        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
        restTemplate.postForEntity(url, request, String.class);

        return code;
    }
    
    public String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }    
    
}