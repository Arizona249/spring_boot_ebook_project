package com.ebookApi.EBook.Helper;

import java.util.List;
import java.util.Optional;
/**
 * <h1>Supported Languages and their Code</h1>
 * English: en<br/>
 * French: fr<br/>
 * German: de<br/>
 * Finnish: fi<br/>
 * Dutch: nl<br/>
 * Portuguese: pt<br/>
 * Chinese: zh<br/>
 * Spanish: es<br/>
 * Italian: it<br/>
 * Greek: el<br/>
 * <br/>
 * <br/>
 * <br/>
 *
 * <h2>Supported Mime Type</h2>
 *  audio<br/>
 *  text<br/>
 *
 * */


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
