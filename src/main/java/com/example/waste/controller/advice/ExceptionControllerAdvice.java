package com.example.waste.controller.advice;

import com.example.waste.exceptions.ValidApiKeyIsRequiredException;
import com.example.waste.model.ErrorDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(ValidApiKeyIsRequiredException.class)
    public ResponseEntity<ErrorDetails> exceptionValidTokenIsRequiredHandler(ValidApiKeyIsRequiredException e){
        ErrorDetails errorDetails = new ErrorDetails();
        errorDetails.setMessage("Valid Api Key is required for this operation");
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(errorDetails);
    }
}
