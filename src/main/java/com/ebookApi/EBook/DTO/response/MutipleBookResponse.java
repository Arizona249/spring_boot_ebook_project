package com.ebookApi.EBook.DTO.response;

import com.ebookApi.EBook.controllers.BookController;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MutipleBookResponse {

    private Long count;
    @Setter(AccessLevel.NONE)
    private String next;
    @Setter(AccessLevel.NONE)
    private String previous;
    private List<LightWeightBookResponse> results;

    public void setNext(String next) {
        this.next = next;
//        if(this.next!=null){
//            this.next=getParamsFromUri(BASE_URL,this.next);
//        }
    }

    public void setPrevious(String previous) {
        this.previous = previous;
//        if(this.previous!=null){
//            this.previous=getParamsFromUri(BASE_URL,this.previous);
//        }
    }


/**
 * @param nextOrPrevious: example= "https://gutendex.com/books/?page=2&topic=children/"
 * @return BASE_URL+nextOrPrevious(?page=2&topic=children)*/
    public String getParamsFromUri(String myAppUri,String nextOrPrevious){
        return myAppUri+nextOrPrevious.substring(27);
    }

}
//"count": 8181,
//        "next": "https://gutendex.com/books/?page=2&topic=children",
//        "previous": null,
//        "results": [