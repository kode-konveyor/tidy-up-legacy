package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestPutUserNotFoundTest extends WorkRequestTestBase {
	@Test
	public void call() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL, "", true, true, true, false, new ArrayList<GrantedAuthority>());

		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> {
			workRequestController.put(USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier(),
					requestDto());
		});
	}
}
