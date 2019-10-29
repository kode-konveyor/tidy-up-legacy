package com.kodekonveyor.tidyup;

import lombok.Getter;

@Getter
public class TidyUserNotFoundException extends RuntimeException {
	
	  private final Long id;

	  public TidyUserNotFoundException(final long id) {
	    super("TidyUser could not be found with id: " + id);
	    this.id = id;
	}
}
