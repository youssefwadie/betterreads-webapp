package com.github.youssefwadie.readwithme.search;

import com.github.youssefwadie.readwithme.util.OpenLibraryUtil;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class SearchService {
    private static final String SEARCH_API_BASE_URL = "http://openlibrary.org/search.json";

    private final WebClient webClient;

    public SearchService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl(SEARCH_API_BASE_URL).build();
    }

    public Mono<SearchResult> search(String query, long maxSize) {
        final Mono<SearchResult> resultsMono = webClient
                .get()
                .uri("?q={query}", query)
                .retrieve()
                .bodyToMono(SearchResult.class);

        return resultsMono
                .map(searchResult -> {
                    val limitedSearchResult = new SearchResult();
                    val limitedDocs = searchResult
                            .getDocs()
                            .stream()
                            .limit(maxSize)
                            .peek(bookResult -> {
                                bookResult.setKey(bookResult.getKey().replace("/works/", ""));
                                val coverUrl = OpenLibraryUtil.coverUrl(bookResult.getCover_i(), "L");
                                bookResult.setCover_i(coverUrl);
                            })
                            .toList();

                    limitedSearchResult.setNumFound(searchResult.getNumFound());
                    limitedSearchResult.setDocs(limitedDocs);
                    return limitedSearchResult;
                });

    }

    public Mono<SearchResult> search(String query) {
        return search(query, 10);
    }
}
