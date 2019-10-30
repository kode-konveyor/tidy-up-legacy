package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class TidyUserAlreadyRegisteredException extends RuntimeException {

	private final String email;

	public TidyUserAlreadyRegisteredException(final String email) {
		super("TidyUser is already registered w/ email: " + email);
		this.email = email;
	}

}
