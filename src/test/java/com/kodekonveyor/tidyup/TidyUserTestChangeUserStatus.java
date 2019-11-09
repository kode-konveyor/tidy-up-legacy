package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserTestChangeUserStatus extends TidyUserTest {
	@Test
	public void call() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(roleRepository.findByName(roleDto().toString())).thenReturn(role());
		when(tidyUserRepository.save(Mockito.any())).thenReturn(user().get());
		ResponseEntity<TidyUserResource> response = tidyUserController.put(USER_IDENTIFIER, userdto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
}
