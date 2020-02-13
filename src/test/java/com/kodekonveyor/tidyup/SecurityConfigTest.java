package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class SecurityConfigTest {

	final private ObjectPostProcessor<Object> objectPostProcessor = new ObjectPostProcessor<Object>() {
		@Override
		public <T> T postProcess(final T object) {
			return object;
		}
	};
	
	final private UserDetailsService userDetailsService = Mockito.mock(UserDetailsService.class);
	final private AuthenticationManagerBuilder auth = Mockito.mock(AuthenticationManagerBuilder.class);

	@Test
	public void testConfigureAuthenticationManagerBuilder() throws Exception {
		String rawPassword = "bar";
		String encodedPassword = new BCryptPasswordEncoder(11).encode(rawPassword);
	    when(userDetailsService.loadUserByUsername("foo")).thenReturn(new User("foo",encodedPassword,true,true,true,true, new ArrayList<GrantedAuthority>()));
		ArgumentCaptor<DaoAuthenticationProvider> capturedProvider = ArgumentCaptor.forClass(DaoAuthenticationProvider.class);
		new SecurityConfig(userDetailsService).configure(this.auth);
		Mockito.verify(this.auth).authenticationProvider(capturedProvider.capture());
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken("foo",rawPassword);
		Authentication auth = capturedProvider.getValue().authenticate(token);
		assertThat(auth.isAuthenticated()).isTrue();
	}
	
	@Test
	public void configure() {
		HttpSecurity http = new HttpSecurity(objectPostProcessor, auth, new java.util.HashMap<Class<? extends Object>, Object> ());
		assertDoesNotThrow( () -> { new SecurityConfig(userDetailsService).configure(http) ; });
	}
	
	@Test
	public void sessionreg() {
		assertThat(new SecurityConfig(userDetailsService).sessionRegistry()).isInstanceOf(SessionRegistry.class);
	}

}
