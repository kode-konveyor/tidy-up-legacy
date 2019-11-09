package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;


public class WorkRequestTestNonExistentForUser extends WorkRequestTestBase {
	@Test
	public void call() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> {
			workRequestController.get(USER_IDENTIFIER,
					user().get().getWorkRequests().iterator().next().getIdentifier() + 1L);
		});
	}
}
