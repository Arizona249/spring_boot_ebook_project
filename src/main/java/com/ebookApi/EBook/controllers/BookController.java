package com.ebookApi.EBook.controllers;

import com.ebookApi.EBook.DTO.ApiResponseFactory;
import com.ebookApi.EBook.DTO.response.ApiResponse;
import com.ebookApi.EBook.DTO.response.MutipleBookResponse;
import com.ebookApi.EBook.DTO.response.SingleBookResponseDTO;
import com.ebookApi.EBook.Helper.BookSearchParams;
import com.ebookApi.EBook.service.BookService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
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

    @GetMapping("")
    public ResponseEntity<ApiResponse<MutipleBookResponse>> getBookBySearchParams(BookSearchParams params) throws IOException, InterruptedException {
        APP_BASE_URL=getAppBaseUrl();
        log.debug("BASE_APP_URL: {}",APP_BASE_URL);
       return ResponseEntity.ok(ApiResponseFactory.createSuccessResponse(service.getBookBySearchParams(params,APP_BASE_URL),"Books Fetched Successfully"));
    }


}
