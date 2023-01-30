package com.github.youssefwadie.readwithme.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {
    @GetMapping("/user")
    public Mono<String> user(@AuthenticationPrincipal Mono<OAuth2User> principleMono) {
        return principleMono.doOnNext(System.out::println)
                .mapNotNull(principle -> principle.getAttribute("name"));
    }
}
