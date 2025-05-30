package com.soldesk6F.ondal.search;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StoreSortTypeConverter implements Converter<String, StoreSortType> {

    @Override
    public StoreSortType convert(String source) {
        return StoreSortType.from(source);
    }
}