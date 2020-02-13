package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class WorkRequestNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 903319701438951429L;
	private final long identifier;

	public WorkRequestNotFoundException(final long identifier) {
		super("WorkRequest could not be found with id: " + identifier);
		this.identifier = identifier;
	}
}
