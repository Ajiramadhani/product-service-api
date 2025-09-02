package com.example.product.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ProductExceptionHandler extends RuntimeException {

    private String messageTech;
    private HttpStatus httpStatus;

    public ProductExceptionHandler(String message, String messageTech, HttpStatus httpStatus){
        super(message);
        this.messageTech = messageTech;
        this.httpStatus = httpStatus;
    }
}
