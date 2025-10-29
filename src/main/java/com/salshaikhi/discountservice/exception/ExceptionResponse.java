package com.salshaikhi.discountservice.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
public class ExceptionResponse {
    List<String> errors;
    int code;
    String status;
    Date time;
}