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
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ResponseEntity<String> handleError() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERR: Midagi läks valesti.");
    }
}
