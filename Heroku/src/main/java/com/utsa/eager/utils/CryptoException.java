package com.utsa.eager.utils;

@SuppressWarnings("serial")
public class CryptoException extends Exception {
	public CryptoException() {
    }
 
    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }

}

