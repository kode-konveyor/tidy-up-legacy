package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class WorkRequestNotFoundException extends RuntimeException {
	  private final long id;

	  public WorkRequestNotFoundException(final long id) {
	    super("WorkRequest could not be found with id: " + id);
	    this.id = id;
	}
}
