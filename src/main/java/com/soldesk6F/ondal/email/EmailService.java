package com.soldesk6F.ondal.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public String sendEmail(String to) {
        String code = String.valueOf((int)(Math.random() * 900000) + 100000);
//        System.out.println("발송되는 인증코드: " + code);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("온달 이메일 인증 코드");
        message.setText("인증 코드: " + code);

        mailSender.send(message);

        return code;
    }
}