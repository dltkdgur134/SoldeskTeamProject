package com.soldesk6F.ondal.search;

import com.soldesk6F.ondal.search.trieLib;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;

@Service
public class TrieAutocompleteService {

    public List<String> suggest(String keyword){
        if(keyword == null || keyword.isBlank()) return List.of();
        String[] raw = trieLib.getSearchList(keyword);
        return raw == null ? List.of()
                           : Arrays.stream(raw).distinct().limit(10).toList();
    }
}