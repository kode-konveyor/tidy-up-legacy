package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class TidyUserAlreadyRegisteredException extends RuntimeException {

	private static final long serialVersionUID = 6497286315192834317L;
	private final String email;

	public TidyUserAlreadyRegisteredException(final String email) {
		super("TidyUser is already registered w/ email: " + email);
		this.email = email;
	}

}
