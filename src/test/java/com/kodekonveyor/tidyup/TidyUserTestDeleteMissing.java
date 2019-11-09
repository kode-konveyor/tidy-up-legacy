package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class TidyUserTestDeleteMissing extends TidyUserTest {
	@Test
	public void call() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> {
			tidyUserController.delete(USER_IDENTIFIER);
		});
	}
}
