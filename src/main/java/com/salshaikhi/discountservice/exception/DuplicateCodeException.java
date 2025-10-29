package com.salshaikhi.discountservice.exception;

import org.springframework.dao.DataIntegrityViolationException;

public class DuplicateCodeException extends DataIntegrityViolationException {
    public DuplicateCodeException(String message) {
        super(message);
    }
}
