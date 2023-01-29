package com.github.youssefwadie.readwithme.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal OAuth2User principle) {
        System.out.println(principle);
        return principle.getAttribute("name");
    }
}
