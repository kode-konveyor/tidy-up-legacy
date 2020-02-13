package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TidyUserControllerAdviceTest {
	@Test
	public void testNotFoundException() {
		assertThat(new TidyUserControllerAdvice()
				.notFoundException(new TidyUserNotFoundException(123_456L))
				.getBody()
				.iterator()
				.next()
				.getMessage()
				.contains("123456"))
		.isTrue();
	}

	@Test
	public void testAssertionException() {
		IllegalArgumentException exception = new IllegalArgumentException("ABC");
		assertThat(new TidyUserControllerAdvice()
				.assertionException(exception)
				.getBody()
				.iterator()
				.next()
				.getMessage()
				.contains("ABC")
				)
		.isTrue();
		
	}

	@Test
	public void testAlreadyRegistered() {
		assertThat(new TidyUserControllerAdvice()
				.alreadyRegistered(new TidyUserAlreadyRegisteredException("XYZ"))
				.getBody()
				.iterator()
				.next()
				.getMessage()
				.endsWith("XYZ"))
		.isTrue();
	}

}
