package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class TidyUserTestDelete extends TidyUserTestBase {
    private ResponseEntity<?> response;
    private ArgumentCaptor<Long> deletedId;

	@BeforeEach
    @Override
	public void setUp() {
		super.setUp();
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		this.deletedId = ArgumentCaptor.forClass(Long.class);
		doNothing().when(tidyUserRepository).deleteById(this.deletedId.capture());

		this.response = tidyUserController.delete(USER_IDENTIFIER);
	}

	@Test
    public void noContentReturned() {
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}

	@Test
	public void deleteCalledWithCorrectId() {
		assertThat(this.deletedId.getValue()).isEqualTo(user().get().getIdentifier());
	}
}
