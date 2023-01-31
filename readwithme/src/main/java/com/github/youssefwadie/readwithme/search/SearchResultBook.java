package com.github.youssefwadie.readwithme.search;

import lombok.Data;

import java.util.List;

/**
 * Represents the structure of a single element in the search result returned by the
 * Open Library API.
 */
@Data
public class SearchResultBook {
    private String key;
    private String title;
    private List<String> author_name;
    private String cover_i;
    private int first_publish_year;

}
