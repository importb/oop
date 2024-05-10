package com.example.phxcsb.exceptions;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class FallbackErrorController implements ErrorController {
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<String> genericErrorHandler() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERR: Midagi läks valesti.");
    }
}