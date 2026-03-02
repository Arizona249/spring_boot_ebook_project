package com.ebookApi.EBook.DTO.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(value = "formats",allowSetters = true)
public record LightWeightBookResponse(Long id,
                                      String title,
                                      List<AuthorRecord> authors,
//                                      List<String> languages,
//                                      @JsonAlias("bookshelves")
//                                      List<String> book_category,
                                      Map<String,String> formats,
                                      String cover_image,
                                      String media_type,
                                      Long download_count)
{

    public LightWeightBookResponse(Long id, String title, List<AuthorRecord> authors,
                                   Map<String, String> formats, String cover_image, String media_type, Long download_count) {
        this.id = id;
        this.title = title;
        this.authors = authors;
        this.formats = formats;
        this.cover_image = extractCoverImage();
        this.media_type = media_type;
        this.download_count = download_count;

    }

    public boolean containsFormat(String key){
        if(formats!=null){
            return formats.containsKey(key);
        }
        return false;
    }

    public String extractCoverImage(){
        return containsFormat("image/jpeg")?formats.get("image/jpeg"):null;
    }



}



