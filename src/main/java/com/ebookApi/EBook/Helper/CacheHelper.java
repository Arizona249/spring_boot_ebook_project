package com.ebookApi.EBook.Helper;

import com.ebookApi.EBook.enums.SearchParam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class CacheHelper {

    private final CacheManager cacheManager;
    private final Logger log=LoggerFactory.getLogger(CacheHelper.class);
    private String cacheKey=null;

    //    Cache search query Data manually because of complex search param key
//    can add the validation dependency to make sure the method parameters are not null and empty
    public   <T> T retrieveOrCacheData(String cacheName,Object key,Class<T> responseType, Supplier<T> fetcher){
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

    public  String getCacheKey(BookSearchParams params){
        cacheKey=null;
        if(params!=null){
            params.search().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+ SearchParam.SEARCH.value+"="+v.strip();
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
            params.ids().ifPresent(v->{
//                this means it has a value already
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.IDS.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.IDS.value+"="+v.strip();
                }
            });
            params.mime_type().ifPresent(v->{
                if(cacheKey!=null){
                    cacheKey+="&"+SearchParam.MIME_TYPES.value+"="+v.strip();
                }
                else if (cacheKey == null || cacheKey.isBlank()) {
                    cacheKey=SearchParam.MIME_TYPES.value+"="+v.strip();
                }
            });
            if(cacheKey==null)cacheKey=SearchParam.SORT.value+"=popular";
            return cacheKey;
        }
        return cacheKey=SearchParam.SORT.value+"=popular";
    }


}
