package com.ebookApi.EBook.service;

import com.ebookApi.EBook.DTO.response.MutipleBookResponse;
import com.ebookApi.EBook.DTO.response.SingleBookResponseDTO;
import com.ebookApi.EBook.Helper.AppHttpClientHelper;
import com.ebookApi.EBook.Helper.BookSearchParams;
import com.ebookApi.EBook.Helper.CacheHelper;
import com.ebookApi.EBook.exception.ApiResourceNotFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpResponse;

@Service
@RequiredArgsConstructor
public class BookService {

    public  final String base_url="https://gutendex.com/books/";
    private final Logger log= LoggerFactory.getLogger(BookService.class);
    final String QUERY_CACHE_NAME1 ="querycache";
    final String SINGLE_BOOK_CACHE_NAME="SingleBookResponseCache";
    @Getter
    @Setter
    public String fileName=null;

    public final CacheHelper cacheHelper;
    public final AppHttpClientHelper httpClientHelper;





    @Cacheable(cacheNames = "SingleBookResponseCache", key = "#id")
    public SingleBookResponseDTO getBookById(Long id) throws IOException, InterruptedException {
        log.info("Inside getBookById method");
        SingleBookResponseDTO requestDTO=httpClientHelper.parseResponse(httpClientHelper.sendGetRequest(UriComponentsBuilder.fromUriString(base_url+id).build().toUri(),String.class), SingleBookResponseDTO.class);
        log.debug("SingleBookResponseObject: {}",requestDTO);
        return requestDTO;

    }

    public MutipleBookResponse getBookBySearchParams(BookSearchParams params,String app_url){
            log.info("BookSearchParams: {}",params);
            log.info("Cache Key From Method Call: {}",cacheHelper.getCacheKey(params));
            var url= httpClientHelper.buildRequestUri(params);

//            log.info("Built URL: {}",url);


//            this returns the cached data  or fetches it and saves it in the cache;
           return cacheHelper.retrieveOrCacheData(QUERY_CACHE_NAME1,cacheHelper.getCacheKey(params), MutipleBookResponse.class,()->{
               String cache_key;
               cache_key=cacheHelper.getCacheKey(params);
                log.info("Cache Key: {}",cache_key);

                MutipleBookResponse response= null;
                try {
                    response = httpClientHelper.parseResponse(httpClientHelper.sendGetRequest(url,String.class), MutipleBookResponse.class);

                }
                catch (IOException | InterruptedException e) {
                    throw new RuntimeException("Something Went Wrong, Please Try Again");
                }
                return setPageNavigationUrl(app_url, response);
            });
//            log.info("Response Before Setting Next: {}",response);
//            log.info("Response After Setting Next: {}",response);

    }


    /**
     * i would send the details of a book when the getBookbyId endpoint is hit and it would contain the formats
     * and when sending a download request the format must be included with that format i can then make a download request
     * */
    public HttpResponse<InputStream> downloadBookById(Long id, String format){
       SingleBookResponseDTO cachedBook= cacheHelper.retrieveOrCacheData(SINGLE_BOOK_CACHE_NAME,id,SingleBookResponseDTO.class, ()->{
                    SingleBookResponseDTO singleBookResponseDTO=null;
                    try {
                        singleBookResponseDTO=httpClientHelper.parseResponse(httpClientHelper.sendGetRequest(URI.create(base_url+id),String.class),SingleBookResponseDTO.class);
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return singleBookResponseDTO;
                });
        log.info("BookFileName form Object: {}",cachedBook.title());
//       log.info("CacheBook:{}",cachedBook);
       HttpResponse<InputStream> stream=null;
       if(cachedBook==null)
           throw new ApiResourceNotFoundException("Book Not Found With Id: "+id);

       log.info("File Extension: {}",extractFileExtension(format));

        this.setFileName(cachedBook.title()+extractFileExtension(format));
        log.info("BookFileName: {}",fileName);


       /*
       * i need to create a method that would send a get request to the download url but would return
       * an inputStream or a generic type in the AppHttpClientHelper class
       * */
        if(cachedBook.extractDownloadLink(format) == null)
            throw new ApiResourceNotFoundException("invalid Download Format: "+format);

        try {
            stream=httpClientHelper.sendGetRequest(URI.create(cachedBook.extractDownloadLink(format).strip()),
                    InputStream.class);


        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return stream;

    }



    private MutipleBookResponse setPageNavigationUrl(String app_url, MutipleBookResponse response) {
        log.debug("MutipleBookResponseObject: {}",response);
        response.setNext(httpClientHelper.getParamsFromUri(app_url, response.getNext()));
        response.setPrevious(httpClientHelper.getParamsFromUri(app_url, response.getPrevious()));
        return response;
    }

//
    public String extractFileExtension(@NotNull String format){
        String extension=".zip";
        switch (format){
            case "text/html"-> extension=".html";

            case "text/plain", "text/plain; charset=utf-8", "text/plain; charset=us-ascii" -> extension=".txt";
            case "application/x-mobipocket-ebook"-> extension=".mobi";
            case "application/epub+zip", "application/epub zip"->extension=".epub";
            case "application/pdf"-> extension=".pdf";
            case "audio/mpeg"->extension=".mp3";
            case "application/postscript"->extension=".ps";
            case "application/rdf+xml", "application/rdf xml"->extension=".rdf";
            case "application/octet-stream"-> extension=".zip";
            case "audio/ogg"->extension=".ogg";
            case "audio/mp4"->extension=".m4b";

        }

        return extension;
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
