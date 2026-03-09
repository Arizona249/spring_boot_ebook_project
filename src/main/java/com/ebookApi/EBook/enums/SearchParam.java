package com.ebookApi.EBook.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SearchParam {
    SORT("sort"),
    SEARCH("search"),
    LANGUAGES("languages"),
    TOPIC("topic"),
    MIME_TYPES("mime_type"),
    PAGE("page"),
    IDS("ids");
    public String value;
}

