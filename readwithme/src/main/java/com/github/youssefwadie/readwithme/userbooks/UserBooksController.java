package com.github.youssefwadie.readwithme.userbooks;

import com.github.youssefwadie.readwithme.book.Book;
import com.github.youssefwadie.readwithme.book.BookRepository;
import com.github.youssefwadie.readwithme.exceptions.ValidationException;
import com.github.youssefwadie.readwithme.user.BooksByUser;
import com.github.youssefwadie.readwithme.user.BooksByUserRepository;
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
    private final BookRepository bookRepository;
    private final BooksByUserRepository booksByUserRepository;

    private final UserBooksValidator validator;

    /**
     * Basic validation is done, but it's not reported, most likely won't be implemented.
     */
    @PostMapping(path = "/addUserBook", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Mono<Rendering> addBookForUser(ServerWebExchange serverRequest) {
        val principalMono = serverRequest.getPrincipal()
                .cast(OAuth2AuthenticationToken.class)
                .map(OAuth2AuthenticationToken::getPrincipal);

        return Mono.zip(serverRequest.getFormData(), principalMono)
                .flatMap(tuple -> {
                    val userBooks = mapFormDataAndPrincipalToUserBooks(tuple);
                    val bookId = userBooks.getKey().getBookId();

                    return bookRepository
                            .findById(bookId)
                            .flatMap(book -> {
                                val renderingMono = Mono.just(Rendering.redirectTo("/books/" + bookId).build());

                                val errors = new BeanPropertyBindingResult(userBooks, "userBooks");
                                validator.validate(userBooks, errors);

                                if (errors.hasErrors()) {
                                    return renderingMono;
                                }

                                val booksByUser = mapBookAndUserBooksToBooksByUser(book, userBooks);
                                return userBooksRepository
                                        .save(userBooks)
                                        .then(booksByUserRepository.save(booksByUser))
                                        .then(renderingMono);

                            })
                            .onErrorReturn(Rendering.redirectTo("/").build());
                });
    }

    public BooksByUser mapBookAndUserBooksToBooksByUser(Book book, UserBooks userBooks) {
        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userBooks.getKey().getUserId());
        booksByUser.setBookId(book.getId());
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(userBooks.getReadingStatus());
        booksByUser.setRating(userBooks.getRating());
        return booksByUser;
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
