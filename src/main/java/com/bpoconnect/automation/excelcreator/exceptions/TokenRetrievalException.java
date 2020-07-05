package com.bpoconnect.automation.excelcreator.exceptions;

public class TokenRetrievalException extends RuntimeException {

	public TokenRetrievalException(Exception e) {
		super(e);
	}

	public TokenRetrievalException(String e) {
		super(e);
	}

}
