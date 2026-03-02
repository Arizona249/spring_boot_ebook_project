package com.ebookApi.EBook.Helper;

import com.ebookApi.EBook.enums.SearchParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequiredArgsConstructor
@Component
public class AppHttpClientHelper {

    public  final String base_url="https://gutendex.com/books/";
    private final HttpClient client=HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
    private final Logger log= LoggerFactory.getLogger(AppHttpClientHelper.class);
    private final ObjectMapper mapper;


    public HttpResponse<String> sendGetRequest(URI url) throws IOException, InterruptedException {
        HttpRequest request= HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response=client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());
        System.out.println(response.body());
        System.out.println(response.uri());
        return response;


    }

    public URI buildRequestUri(BookSearchParams params){
        if(params!=null){
            var uri= UriComponentsBuilder.fromUriString(base_url);
//            this would avoid adding params if it was not provided
            params.search().ifPresent(v->{
                uri.queryParam(SearchParam.SEARCH.value,v.strip());
            });
            params.sort().ifPresent(v->{
                uri.queryParam(SearchParam.SORT.value,v.strip());
            });
            params.topic().ifPresent(v->{
                uri.queryParam(SearchParam.TOPIC.value,v.strip());
            });
//                    can be 1,2
            params.languages().ifPresent(v->{
                uri.queryParam(SearchParam.LANGUAGES.value,v.strip());
            });
            params.page().ifPresent(v->{
                uri.queryParam(SearchParam.PAGE.value,v.strip());
            });
            return uri.build()
                    .encode()
                    .toUri();

        }
        return UriComponentsBuilder
                .fromUriString(base_url)
                .queryParam(SearchParam.SORT.value,"popular")
                .build()
                .toUri();

    }

    public <T> T parseResponse(HttpResponse<String> response, Class<T> type){

        log.info("Response Body Before Modification: {}",response.body());
        if(response.statusCode()>=200 && response.statusCode()<300) {
            log.info("Response Body Before Modification: {}",response.body());
            try {
                return mapper.readValue(response.body(),type);
            }
            catch(Exception e){
                throw new RuntimeException("Failed to parse response: "+e);
            }
        }
        else {
            throw new RuntimeException("Something Went Wrong, Please try again");
        }
    }

    public String getParamsFromUri(String myAppUri,String nextOrPrevious){
        if(nextOrPrevious!=null)
            return myAppUri+"/api/books"+nextOrPrevious.substring(27);
        return null;
    }

}
