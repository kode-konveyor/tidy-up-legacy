package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class WorkRequestNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 903319701438951429L;
	private final long id;

	public WorkRequestNotFoundException(final long id) {
		super("WorkRequest could not be found with id: " + id);
		this.id = id;
	}
}
