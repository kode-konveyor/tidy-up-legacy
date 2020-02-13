package com.kodekonveyor.tidyup;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@ControllerAdvice(assignableTypes = WorkRequestController.class)
@RequestMapping(produces = "application/vnd.error+json")
public class WorkRequestControllerAdvice {

	@ExceptionHandler(WorkRequestNotFoundException.class)
	public ResponseEntity<VndErrors> notFoundException(final WorkRequestNotFoundException exception) {
		return error(exception, HttpStatus.NOT_FOUND, String.valueOf(exception.getIdentifier()));
	}

	private <E extends Exception> ResponseEntity<VndErrors> error(final E exception, final HttpStatus httpStatus,
			final String logRef) {
		final String message = Optional.of(exception.getMessage()).orElse(exception.getClass().getSimpleName());
		return new ResponseEntity<>(new VndErrors(logRef, message), httpStatus);
	}

}