package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.Test;

public class TidyUserTestAlreadyRegisteredUser extends TidyUserTestBase {
	@Test
	public void call() {
		when(tidyUserRepository.findByEmail(userdto().getEmail())).thenReturn(user());
		assertThrows(TidyUserAlreadyRegisteredException.class, () -> {
			tidyUserController.post(userdto());
		});
	}
}
