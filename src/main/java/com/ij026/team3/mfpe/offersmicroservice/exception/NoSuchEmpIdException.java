package com.ij026.team3.mfpe.offersmicroservice.exception;

public class NoSuchEmpIdException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoSuchEmpIdException() {
	}

	public NoSuchEmpIdException(String message) {
		super(message);
	}
}
