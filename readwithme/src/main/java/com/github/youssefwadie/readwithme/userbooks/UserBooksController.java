package com.github.youssefwadie.readwithme.userbooks;

import com.github.youssefwadie.readwithme.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.result.view.Rendering;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.LocalDate;
import java.util.Objects;

@RequiredArgsConstructor
@Controller
public class UserBooksController {
    private final UserBooksRepository userBooksRepository;
    private final UserBooksValidator validator;

    /**
     * Basic validation is done, but it's not reported, most likely it won't be implemented.
     */
    @PostMapping(path = "/addUserBook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<Rendering> addBookForUser(ServerWebExchange serverRequest) {
        val principalMono = serverRequest.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .map(OAuth2AuthenticationToken::getPrincipal);

        return Mono.zip(serverRequest.getFormData(), principalMono)
                .flatMap(tuple -> {
                    val userBooks = mapFormDataAndPrincipalToUserBooks(tuple);

                    val renderingMono = Mono.just(Rendering.redirectTo("/books/" + userBooks.getKey().getBookId()).build());

                    val errors = new BeanPropertyBindingResult(userBooks, "userBooks");
                    validator.validate(userBooks, errors);

                    if (errors.hasErrors()) {
                        return renderingMono;
                    }

                    return userBooksRepository
                            .save(userBooks)
                            .then(renderingMono);
                });
    }


    @ExceptionHandler(ValidationException.class)
    public Mono<Rendering> onException(ValidationException ex) {
        System.out.println(ex.getErrors());
        return Mono.just(Rendering.view("index").build());
    }

    private UserBooks mapFormDataAndPrincipalToUserBooks(Tuple2<MultiValueMap<String, String>, OAuth2User> tuple) {
        val formData = tuple.getT1();
        val principal = tuple.getT2();

        val startDate = parseLocalDate(formData.getFirst("startDate"));
        val completedDate = parseLocalDate(formData.getFirst("completedDate"));
        val status = formData.getFirst("readingStatus");
        val rating = parseRating(formData.getFirst("rating"));

        val userBooks = new UserBooks();

        userBooks.setCompletedDate(completedDate);
        userBooks.setStartedDate(startDate);
        userBooks.setRating(rating);
        userBooks.setReadingStatus(status);

        if (Objects.isNull(principal.getAttribute("login"))) {
            throw new IllegalStateException("loginId not found.");
        }

        val key = new UserBooksPrimaryKey();
        key.setUserId(principal.getAttribute("login"));
        key.setBookId(formData.getFirst("bookId"));
        userBooks.setKey(key);

        return userBooks;

    }


    private Integer parseRating(String text) {
        if (text == null) return null;
        return Integer.parseInt(text);
    }

    private LocalDate parseLocalDate(String text) {
        if (text == null) return null;
        return LocalDate.parse(text);
    }

}
