package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestCreateOkTest extends WorkRequestTestBase {

	private ResponseEntity<WorkRequestResource> response;
	private WorkRequest savedWorkrequest;

	@BeforeEach
    @Override
	public void setUp() {
		super.setUp();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenAnswer(
				invocation ->
				{
					this.savedWorkrequest = invocation.getArgumentAt(0, WorkRequest.class);
					return user().get().getWorkRequests().iterator().next();
				}
		);

		User user = new User(USER_EMAIL, "", true, true, true, false, new ArrayList<GrantedAuthority>());

		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		this.response = workRequestController.post(USER_IDENTIFIER, requestDto());
	}

	@Test
    public void created() {
		assertThat(this.response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	@Test
	public void hasEmail() {
		assertThat(this.savedWorkrequest.getUser().getEmail()).isEqualTo(user().get().getEmail());
	}

	@Test
	public void hasCity() {
		assertThat(this.savedWorkrequest.getCity()).isEqualTo(user().get().getWorkRequests().iterator().next().getCity());
	}

	@Test
	public void hasDescription() {
		assertThat(this.savedWorkrequest.getDescription()).isEqualTo(user().get().getWorkRequests().iterator().next().getDescription());
	}
}
