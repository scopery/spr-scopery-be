package com.company.scopery.common.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCatalog {
    String code();
    String defaultMessage();
    HttpStatus httpStatus();
}
