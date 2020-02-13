package com.kodekonveyor.tidyup;

import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TidyUserDetailsServiceTest extends TidyUserTestBase {

	@Test
	public void testLoadUserByUsername() {
		when(tidyUserRepository.findByEmail(user().get().getEmail())).thenReturn(user());
		assertThat(new TidyUserDetailsService(tidyUserRepository)
				.loadUserByUsername(user().get().getEmail())
				.getUsername())
		.isEqualTo(user().get().getEmail());
	}
	
	@Test
	public void testLoadUserByUsername2() {
		when(tidyUserRepository.findByEmail(user().get().getEmail())).thenReturn(Optional.empty());
		assertThrows(UsernameNotFoundException.class, () -> {
			new TidyUserDetailsService(tidyUserRepository)
				.loadUserByUsername(user().get().getEmail()); });
	}

	@Test
	public void testLoadUserByUsernameAuthorities() {
		when(tidyUserRepository.findByEmail(user().get().getEmail())).thenReturn(user());
		assertThat(new TidyUserDetailsService(tidyUserRepository)
				.loadUserByUsername(user().get().getEmail())
				.getAuthorities().size())
				.isEqualTo(1);
	}

}