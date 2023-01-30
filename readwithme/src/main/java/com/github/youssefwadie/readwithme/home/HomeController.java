package com.github.youssefwadie.readwithme.home;

import com.github.youssefwadie.readwithme.user.BooksByUser;
import com.github.youssefwadie.readwithme.user.BooksByUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
@Controller
public class HomeController {
    private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id";
    private final BooksByUserRepository booksByUserRepository;

    @GetMapping("/")
    public Mono<Rendering> home(@AuthenticationPrincipal Mono<OAuth2User> principalMono) {
        if (principalMono == null) {
            return Mono.just(Rendering.view("index").build());
        }

        return principalMono.flatMap(this::mapPrincipalToRenderingMono);
    }


    private Mono<Rendering> mapPrincipalToRenderingMono(OAuth2User principal) {
        final String loginId = principal.getAttribute("login");
        return booksByUserRepository
                .findAllById(loginId, CassandraPageRequest.of(0, 100))
                .map(this::mapBooksSliceToRendering);
    }

    private Rendering mapBooksSliceToRendering(Slice<BooksByUser> booksSlice) {
        val booksByUser = booksSlice
                .getContent()
                .stream()
                .distinct()
                .peek(book -> {
                    var coverImageUrl = "/images/no-image.png";
                    if (book.getCoverIds() != null && !book.getCoverIds().isEmpty()) {
                        coverImageUrl = String.format("%s/%s-M.jpg", COVER_IMAGE_ROOT, book.getCoverIds().get(0));
                    }
                    book.setCoverUrl(coverImageUrl);
                })
                .toList();

        return Rendering.view("home")
                .modelAttribute("books", booksByUser)
                .build();
    }

}
