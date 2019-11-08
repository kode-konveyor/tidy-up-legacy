package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class TidyUserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 4042582747892645032L;
	private final Long identifier;

	public TidyUserNotFoundException(final long identifier) {
		super("TidyUser could not be found with id: " + identifier);
		this.identifier = identifier;
	}
}
