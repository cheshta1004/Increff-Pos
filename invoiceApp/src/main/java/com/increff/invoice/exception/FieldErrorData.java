package com.increff.invoice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FieldErrorData {
    private String field;
    private String message;

    public FieldErrorData(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
