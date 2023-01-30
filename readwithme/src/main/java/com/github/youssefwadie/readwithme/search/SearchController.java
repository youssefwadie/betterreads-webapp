package com.github.youssefwadie.readwithme.search;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Controller
@RequestMapping("search")
public class SearchController {
    private static final String SEARCH_TEMPLATE_NAME = "search";
    private final SearchService searchService;

    @GetMapping(path = "", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Rendering> getSearchResults(@RequestParam("query") String query) {
        return searchService.search(query)
                .map(this::mapSearchResultsToRendering);
    }

    private Rendering mapSearchResultsToRendering(SearchResult searchResult) {
        return Rendering.view(SEARCH_TEMPLATE_NAME)
                .modelAttribute("searchResults", searchResult.getDocs())
                .modelAttribute("numFound", searchResult.getNumFound())
                .build();
    }
}
