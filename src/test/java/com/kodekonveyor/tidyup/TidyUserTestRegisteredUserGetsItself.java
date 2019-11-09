package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

public class TidyUserTestRegisteredUserGetsItself extends TidyUserTest {
	@Test
	public void registeredUserGetsItself() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		ResponseEntity<TidyUserResource> response = tidyUserController.get(USER_IDENTIFIER);

		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));

	}
}
