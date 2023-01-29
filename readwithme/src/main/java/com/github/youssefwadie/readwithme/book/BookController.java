package com.github.youssefwadie.readwithme.book;


import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.reactive.result.view.Rendering;
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
    public Mono<Rendering> getBook(@PathVariable(name = "bookId") String bookId,
                                   @AuthenticationPrincipal Mono<OAuth2User> principalMono) {
        return bookRepository.findById(bookId)
                .flatMap(book -> mapBookAndPrincipalToView(book, principalMono))
                .switchIfEmpty(bookNotFound());
    }

    private Mono<Rendering> mapBookAndPrincipalToView(Book book, Mono<OAuth2User> principalMono) {

        var coverImageUrl = "/images/no-image.png";

        if (book.getCoversId() != null && !book.getCoversId().isEmpty()) {
            coverImageUrl = String.format("%s/%s-L.jpg", COVER_IMAGE_ROOT, book.getCoversId().get(0));
        }

        val renderingBuilder = Rendering.view(BOOK_TEMPLATE_NAME);

        renderingBuilder.modelAttribute("coverImage", coverImageUrl);
        renderingBuilder.modelAttribute("book", book);

        return addPrincipal(renderingBuilder, principalMono);
    }

    private Mono<Rendering> addPrincipal(Rendering.Builder<?> renderingBuilder, Mono<OAuth2User> principalMono) {
        if (principalMono == null) return Mono.just(renderingBuilder.build());

        return principalMono.handle((OAuth2User oAuth2User, SynchronousSink<Rendering> synchronousSink) -> {
            val loginId = oAuth2User.getAttribute("login");
            if (Objects.nonNull(loginId)) {
                renderingBuilder.modelAttribute("loginId", loginId);
            }
            synchronousSink.next(renderingBuilder.build());
        });

    }

    private Mono<Rendering> bookNotFound() {
        return Mono.fromSupplier(() -> Rendering.view(BOOK_NOT_FOUND_TEMPLATE_NAME).build());
    }
}
