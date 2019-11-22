package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityConfigTest {

	final private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
		public <T> T postProcess(final T object) {
			return object;
		}
	};
	
	final private UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
	final private AuthenticationManagerBuilder auth = Mockito.mock(AuthenticationManagerBuilder.class);

	@Test
	public void testConfigureAuthenticationManagerBuilder() throws Exception {
		assertDoesNotThrow( () -> { new SecurityConfig(userDetailsService).configure(auth);});
	}
	
	@Test
	public void configurue() {
		HttpSecurity http = new HttpSecurity(objectPostProcessor, auth, new java.util.HashMap<Class<? extends Object>, Object> ());
		assertDoesNotThrow( () -> { new SecurityConfig(userDetailsService).configure(http) ; });
	}
	
	@Test
	public void sessionreg() {
		assertThat(new SecurityConfig(userDetailsService).sessionRegistry()).isInstanceOf(SessionRegistry.class);
	}

}
