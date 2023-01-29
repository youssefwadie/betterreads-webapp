package com.github.youssefwadie.readwithme.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    @Autowired
    protected SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
        http.authorizeExchange(
                auth -> {
                    auth.pathMatchers("/user").authenticated();

                    auth.anyExchange().permitAll();
                }
            );

//        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.csrf(c -> c.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()));
        http.oauth2Login(Customizer.withDefaults());
        http.logout(l -> l.logoutUrl("/"));

        return http.build();
    }

}
