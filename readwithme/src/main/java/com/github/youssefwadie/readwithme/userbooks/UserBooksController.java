package com.github.youssefwadie.readwithme.userbooks;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
public class UserBooksController {
    @PostMapping("/addUserBook")
    public Mono<String> addBookForUser(@AuthenticationPrincipal Mono<OAuth2User> principalMono) {

        return null;
    }
}
