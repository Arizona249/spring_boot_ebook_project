package com.ebookApi.EBook.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class HomeController {
    @GetMapping("/")
    public ResponseEntity<String> checkHealth(){
        return ResponseEntity.ok("OK");
    }
}
