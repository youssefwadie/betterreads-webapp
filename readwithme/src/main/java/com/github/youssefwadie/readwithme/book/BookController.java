package com.github.youssefwadie.readwithme.book;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.util.Objects;

@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookController {
    private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id";
    private static final String BOOK_TEMPLATE_NAME = "book";

    private static final String BOOK_NOT_FOUND_TEMPLATE_NAME = "book-not-found";

    private final BookRepository bookRepository;

    @GetMapping(path = "/{bookId}", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> getBook(@PathVariable(name = "bookId") String bookId, Model model,
                                @AuthenticationPrincipal Mono<OAuth2User> principalMono) {

        return bookRepository.findById(bookId)
                .flatMap(book -> {
                    var coverImageUrl = "/images/no-image.png";

                    if (book.getCoversId() != null && !book.getCoversId().isEmpty()) {
                        coverImageUrl = String.format("%s/%s-L.jpg", COVER_IMAGE_ROOT, book.getCoversId().get(0));
                    }

                    model.addAttribute("coverImage", coverImageUrl);
                    model.addAttribute("book", book);

                    if (principalMono == null) return Mono.just(BOOK_TEMPLATE_NAME);

                    return principalMono.handle((OAuth2User oAuth2User, SynchronousSink<String> synchronousSink) -> {
                        if (Objects.nonNull(oAuth2User.getAttribute("login"))) {
                            model.addAttribute("loginId", oAuth2User.getAttribute("login"));
                        }
                        synchronousSink.next(BOOK_TEMPLATE_NAME);
                    });
                })
                .defaultIfEmpty(BOOK_NOT_FOUND_TEMPLATE_NAME);
    }

}
