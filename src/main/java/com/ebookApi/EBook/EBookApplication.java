package com.ebookApi.EBook;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableCaching
public class EBookApplication {

    public static void main(String[] args) {
        SpringApplication.run(EBookApplication.class, args);
    }

    @Bean
    public CacheManager getCacheManger(){
        return  new ConcurrentMapCacheManager();
    }



}
