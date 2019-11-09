package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class WorkRequestTestForUserNotFound extends WorkRequestTest {
	@Test
	public void call() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		assertThrows(WorkRequestNotFoundException.class, () -> {
			workRequestController.get(USER_IDENTIFIER,
					user().get().getWorkRequests().iterator().next().getIdentifier() + 1L);
		});
	}
}
