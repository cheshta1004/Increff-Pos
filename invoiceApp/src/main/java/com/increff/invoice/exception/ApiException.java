package com.increff.invoice.exception;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@ResponseStatus(HttpStatus.BAD_REQUEST)
@Setter
@Getter
public class ApiException extends Exception {

	private ApiStatus status;
	private List<FieldErrorData> fieldErrors;

	// Basic constructor
	public ApiException(String message) {
		super(message);
		this.status = ApiStatus.INTERNAL_ERROR;
	}

	// Constructor for validation errors
	public ApiException(String message, List<FieldErrorData> fieldErrors) {
		super(message);
		this.status = ApiStatus.VALIDATION_ERROR;
		this.fieldErrors = fieldErrors;
	}

	public ApiStatus getStatus() {
		return status;
	}

	public List<FieldErrorData> getFieldErrors() {
		return fieldErrors;
	}
}



