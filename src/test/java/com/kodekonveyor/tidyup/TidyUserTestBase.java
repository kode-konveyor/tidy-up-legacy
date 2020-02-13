package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TidyUserTestBase extends TestBase {
	protected final TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	protected final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	protected final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	protected TidyUserController tidyUserController;

	@BeforeEach
	public void setUp() {
		this.tidyUserController = new TidyUserController(tidyUserRepository, roleRepository, passwordEncoder);
	}
}
