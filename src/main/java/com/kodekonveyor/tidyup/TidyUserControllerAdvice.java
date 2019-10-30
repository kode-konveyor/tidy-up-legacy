package com.kodekonveyor.tidyup;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;

@ControllerAdvice
@RequestMapping(produces = "application/vnd.error+json")
public class TidyUserControllerAdvice extends ResponseEntityExceptionHandler {
	  @ExceptionHandler(TidyUserNotFoundException.class)
	  public ResponseEntity<VndErrors> notFoundException(final TidyUserNotFoundException e) {
	    return error(e, HttpStatus.NOT_FOUND, e.getId().toString());
	  }

	  private ResponseEntity<VndErrors> error(
	      final Exception exception, final HttpStatus httpStatus, final String logRef) {
	    final String message =
	        Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
	    return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
	  }

	  @ExceptionHandler(IllegalArgumentException.class)
	  public ResponseEntity<VndErrors> assertionException(final IllegalArgumentException e) {
	    return error(e, HttpStatus.NOT_FOUND, e.getLocalizedMessage());
	    

	}
	  @ExceptionHandler(TidyUserAlreadyRegisteredException.class)
	  public ResponseEntity<VndErrors> alreadyRegistered(final TidyUserAlreadyRegisteredException e) {
	    return error(e, HttpStatus.CONFLICT, e.getEmail());
	  }    
    
}
