package com.soldesk6F.ondal.login.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginViewController {
    @GetMapping("/login/login")
    public String showLogin() {
        return "login/login";   // templates/login/login.html
    }
}
