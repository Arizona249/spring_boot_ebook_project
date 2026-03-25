package com.ebookApi.EBook.config.cache_config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Bean
    public Caffeine caffeineConfig(){
        return Caffeine.newBuilder()
                .maximumSize(5000L);
    }
    @Bean
    public CacheManager cacheManager(Caffeine caffeine){
        CaffeineCacheManager cacheManager=new CaffeineCacheManager();
         cacheManager.setCaffeine(caffeine);
         return cacheManager;

    }
}
