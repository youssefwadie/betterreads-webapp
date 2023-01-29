package com.github.youssefwadie.readwithme.search;

import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class SearchService {
    private static final String SEARCH_API_BASE_URL = "http://openlibrary.org/search.json";
    private static final String COVER_IMAGE_ROOT = "https://covers.openlibrary.org/b/id";

    private final WebClient webClient;

    public SearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(SEARCH_API_BASE_URL).build();
    }

    public SearchResult search(String query, long maxSize) {
        final Mono<SearchResult> resultsMono = this
                .webClient
                .get()
                .uri("?q={query}", query)
                .retrieve()
                .bodyToMono(SearchResult.class);
        val fullSearchResult = resultsMono.block();
        if (fullSearchResult == null) return null;

        val limitedSearchResult = new SearchResult();

        val limitedDocs = fullSearchResult
                .getDocs()
                .stream()
                .limit(maxSize)
                .peek(bookResult -> {
                    bookResult.setKey(bookResult.getKey().replace("/works/", ""));
                    String coverId = bookResult.getCover_i();
                    if (StringUtils.hasText(coverId)) {
                        coverId = String.format("%s/%s-L.jpg", COVER_IMAGE_ROOT, coverId);
                    } else {
                        coverId = "/images/no-image.png";
                    }
                    bookResult.setCover_i(coverId);
                })
                .toList();

        limitedSearchResult.setNumFound(fullSearchResult.getNumFound());
        limitedSearchResult.setDocs(limitedDocs);

        return limitedSearchResult;
    }
    public SearchResult search(String query) {
        return search(query, 10);
    }
}
