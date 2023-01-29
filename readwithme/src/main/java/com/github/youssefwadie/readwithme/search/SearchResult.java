package com.github.youssefwadie.readwithme.search;

import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private int numFound;
    private List<SearchResultBook> docs;

}
