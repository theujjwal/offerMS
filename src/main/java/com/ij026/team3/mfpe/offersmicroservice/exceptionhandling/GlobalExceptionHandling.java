package com.ij026.team3.mfpe.offersmicroservice.exceptionhandling;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandling {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<String> handleExceptions(Exception exception) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
	}
}
