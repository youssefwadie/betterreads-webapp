package com.github.youssefwadie.readwithme.search;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping(path = "", produces = MediaType.TEXT_HTML_VALUE)
    public String getSearchResults(@RequestParam("query") String query, Model model) {
        val searchResult = searchService.search(query);
        model.addAttribute("searchResults", searchResult.getDocs());
        model.addAttribute("numFound", searchResult.getNumFound());
        return "search";
    }
}
