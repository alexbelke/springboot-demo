package com.alexbelke.springbootdemo.util.exceptions;

public class OrderNotFoundException extends RuntimeException {
	public OrderNotFoundException(Long id) {
		super("Could not find the order " + id);
	}
}
