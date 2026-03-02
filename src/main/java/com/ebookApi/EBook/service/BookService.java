package com.ebookApi.EBook.service;

import com.ebookApi.EBook.DTO.response.MutipleBookResponse;
import com.ebookApi.EBook.DTO.response.SingleBookResponseDTO;
import com.ebookApi.EBook.Helper.AppHttpClientHelper;
import com.ebookApi.EBook.Helper.BookSearchParams;
import com.ebookApi.EBook.Helper.CacheHelper;
import com.ebookApi.EBook.enums.SearchParam;
import com.ebookApi.EBook.exception.InternalServerError;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class BookService {

    public  final String base_url="https://gutendex.com/books/";
    private final Logger log= LoggerFactory.getLogger(BookService.class);
    final String QUERY_CACHE_NAME="querycache";

    public final CacheHelper cacheHelper;
    public final AppHttpClientHelper httpClientHelper;





    @Cacheable(cacheNames = "SingleBookResponseCache", key = "#id")
    public SingleBookResponseDTO getBookById(Long id) throws IOException, InterruptedException {
        log.info("Inside getBookById method");
        SingleBookResponseDTO requestDTO=httpClientHelper.parseResponse(httpClientHelper.sendGetRequest(UriComponentsBuilder.fromUriString(base_url+id).build().toUri()), SingleBookResponseDTO.class);
        log.debug("SingleBookResponseObject: {}",requestDTO);
        return requestDTO;

    }

    public MutipleBookResponse getBookBySearchParams(BookSearchParams params,String app_url){
//            log.debug("BookSearchParams: {}",params);
            var url= httpClientHelper.buildRequestUri(params);
            var cache_key=cacheHelper.getCacheKey(params);
//            log.info("Built URL: {}",url);
            log.info("Cache Key: {}",cache_key);

//            this returns the cached data  or fetches it and saves it in the cache;
           return cacheHelper.retrieveOrCacheData(QUERY_CACHE_NAME,cache_key, MutipleBookResponse.class,()->{
                MutipleBookResponse response= null;
                try {
                    response = httpClientHelper.parseResponse(httpClientHelper.sendGetRequest(url), MutipleBookResponse.class);

                }
                catch (IOException | InterruptedException e) {
                    throw new RuntimeException("Something Went Wrong, Please Try Again");
                }
                return setPageNavigationUrl(app_url, response);
            });
//            log.info("Response Before Setting Next: {}",response);
//            log.info("Response After Setting Next: {}",response);

    }



    private MutipleBookResponse setPageNavigationUrl(String app_url, MutipleBookResponse response) {
        log.debug("MutipleBookResponseObject: {}",response);
        response.setNext(httpClientHelper.getParamsFromUri(app_url, response.getNext()));
        response.setPrevious(httpClientHelper.getParamsFromUri(app_url, response.getPrevious()));
        return response;
    }

/*    //    Cache search query Data manually because of complex search param key
//    can add the validation dependency to make sure the method parameters are not null and empty
    public <T> T retrieveOrCacheData(String cacheName,Object key,Class<T> responseType, Supplier<T> fetcher){
//        this returns the cache object with the particular name
        var cache=cacheManager.getCache(cacheName);
        log.info("Cache Key: {}",key);

//      would be null if cache does not exist with the name passed
//        outcome: create a new cache with the provided name
        if(cache==null){
            cacheManager.getCacheNames().add(cacheName);
            cache=cacheManager.getCache(cacheName);
        }
        T cachedResponse=cache.get(key, responseType);
        if(cachedResponse!=null)
            return cachedResponse;

        cachedResponse=fetcher.get();
        cache.put(key,cachedResponse);
        return cachedResponse;


    }

    public String getCacheKey(BookSearchParams params){
        cacheKey=null;
        if(params!=null){
            params.search().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.SEARCH.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.SEARCH.value+"="+v.strip();
                }
            });
            params.sort().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.SORT.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.SORT.value+"="+v.strip();
                }
            });
            params.topic().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.TOPIC.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.TOPIC.value+"="+v.strip();
                }
            });
            params.languages().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.LANGUAGES.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.LANGUAGES.value+"="+v.strip();
                }
            });
            params.page().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.PAGE.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.PAGE.value+"="+v.strip();
                }
            });
            return cacheKey;
        }
        return cacheKey=SearchParam.SORT.value+"=popular";
    }

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
    }*/
}
