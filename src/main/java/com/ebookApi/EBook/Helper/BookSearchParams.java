package com.ebookApi.EBook.Helper;

import java.util.List;
import java.util.Optional;

public record BookSearchParams(Optional<String> search,
                               Optional<String> languages,
                               Optional<String> sort,
                               Optional<String> topic,
                               Optional<String> mime_type,
                               Optional<String> page
) {
    public BookSearchParams(Optional<String> search, Optional<String> sort, Optional<String> topic, Optional<String> mime_type,Optional<String> page) {
        this(search, Optional.of("en"), Optional.of("popular"), topic, mime_type,page);
    }
}
