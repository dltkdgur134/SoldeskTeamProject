package com.soldesk6F.ondal.chat.controller;

import com.soldesk6F.ondal.chat.dto.ChatMessageDto;
import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.owner.order.OrderService;
import com.soldesk6F.ondal.useract.order.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.soldesk6F.ondal.login.OAuth2LoginSuccessHandler;

import java.security.Principal;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    }
