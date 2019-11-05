package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TidyUserTest {
	private static final long _42L = 42L;
	private TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	private PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	private TidyUserController tidyUserController;

	@BeforeEach
	void setup() {
		this.tidyUserController = new TidyUserController(tidyUserRepository, roleRepository, passwordEncoder);
	}

	@Test
	void registeredUserGetsItself() {
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		ResponseEntity<TidyUserResource> response = tidyUserController.get(_42L);

		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));

	}

	private Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setId(_42L);
		return Optional.of(user);
	}

}
