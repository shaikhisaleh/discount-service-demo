package com.salshaikhi.discountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleNotFoundException(NotFoundException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(errors,
                HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), new Date());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream().map(
                        error ->{
                            String errorMessage = "";
                            errorMessage+= error.getField();
                            errorMessage+=" : ";
                            errorMessage+=error.getDefaultMessage();
                          return errorMessage;
                        }).toList();
        ExceptionResponse exceptionResponse = new ExceptionResponse(errors,
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), new Date());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DuplicateCodeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Object> handleDuplicateCodeException(DuplicateCodeException ex) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getMessage());
        ExceptionResponse exceptionResponse = new ExceptionResponse(errors,
                HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(), new Date());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

}
