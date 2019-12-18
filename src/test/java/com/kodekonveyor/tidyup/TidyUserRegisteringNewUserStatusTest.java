package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserRegisteringNewUserStatusTest extends TidyUserTestBase {
	@Test
	public void call() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
		when(roleRepository.findByName(userdto().getRole().toString())).thenReturn(role());
		when(tidyUserRepository.save(Mockito.any())).thenReturn(user().get());

		ResponseEntity<TidyUserResource> response = tidyUserController.post(userdto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	}
}
