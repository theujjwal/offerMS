package com.ij026.team3.mfpe.offersmicroservice.exceptionhandling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.RetryableException;

@RestControllerAdvice
public class ApplicationExceptionHandler {

	@ExceptionHandler(RetryableException.class)
	public ResponseEntity<String> handleRetryableException(RetryableException exception) {
		return ResponseEntity.badRequest().body(exception.getLocalizedMessage());
	}

}
