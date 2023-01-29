package com.github.youssefwadie.readwithme.book;


import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("/books")
public class BookController {
    private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id";
    private final BookRepository bookRepository;

    @GetMapping(path = "/{bookId}", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> getBook(@PathVariable(name = "bookId") String bookId, Model model) {
        return bookRepository.findById(bookId)
                .map(book -> {
                    var coverImageUrl = "/images/no-image.png";

                    if (book.getCoversId() != null && !book.getCoversId().isEmpty()) {
                        coverImageUrl = String.format("%s/%s-L.jpg", COVER_IMAGE_ROOT, book.getCoversId().get(0));
                    }

                    model.addAttribute("coverImage", coverImageUrl);
                    model.addAttribute("book", book);
                    return "book";
                })
                .onErrorReturn("book-not-found");
    }

}
