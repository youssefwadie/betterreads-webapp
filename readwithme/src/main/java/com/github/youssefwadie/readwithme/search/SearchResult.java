package com.github.youssefwadie.readwithme.search;

import lombok.Data;

import java.util.List;

/**
 * Represents the structure of the search result returned by the
 * Open Library API.
 */
@Data
public class SearchResult {
    private int numFound;
    private List<SearchResultBook> docs;

}
