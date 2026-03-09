package com.ebookApi.EBook.controllers;

import com.ebookApi.EBook.DTO.ApiResponseFactory;
import com.ebookApi.EBook.DTO.response.ApiResponse;
import com.ebookApi.EBook.DTO.response.MutipleBookResponse;
import com.ebookApi.EBook.DTO.response.SingleBookResponseDTO;
import com.ebookApi.EBook.Helper.BookSearchParams;
import com.ebookApi.EBook.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Arrays;

@RequiredArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("api/books")
public class BookController {

    private final BookService service;

    public  String APP_BASE_URL= null;
    private final Logger log= LoggerFactory.getLogger(BookController.class);

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SingleBookResponseDTO>> getBookByid(@PathVariable Long id) throws IOException, InterruptedException {
        APP_BASE_URL= getAppBaseUrl();
       return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse(service.getBookById(id),"Book Fetched Successfully"));
    }

    private  String getAppBaseUrl() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MutipleBookResponse>> getBookBySearchParams( BookSearchParams params) {
        APP_BASE_URL=getAppBaseUrl();
        log.debug("BASE_APP_URL: {}",APP_BASE_URL);
       return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse(service.getBookBySearchParams(params,APP_BASE_URL),"Books Fetched Successfully"));
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadBookById(@RequestParam(name = "id") Long id,
                                                     @RequestParam(name="format") String format){
        var streamHttpResponse =service.downloadBookById(id,format);
        var headers=streamHttpResponse.headers();
//        use this class for tricky file names with space
        ContentDisposition disposition= ContentDisposition
                .attachment()
                .filename(service.getFileName())
                .build();

        log.info("BookFileName: "+disposition);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, headers.allValues(HttpHeaders.CONTENT_TYPE).getFirst())
                .header(HttpHeaders.CONTENT_LENGTH,headers.allValues(HttpHeaders.CONTENT_LENGTH).getFirst())
                .header(HttpHeaders.CONTENT_DISPOSITION,disposition.toString())
                .body(new InputStreamResource(streamHttpResponse.body()));

    }


}
