package com.soldesk6F.ondal.search;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AutocompleteController {

    private final TrieAutocompleteService service;

    public AutocompleteController(TrieAutocompleteService service) {
        this.service = service;
    }

    /** GET /autocomplete?query=치 */
    @GetMapping("/autocomplete")
    public List<String> autocomplete(@RequestParam("query") String query) {
        return service.suggest(query);
    }
}
