package com.kodekonveyor.tidyup;


import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestTest {
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	private static final String USER_EMAIL = "nobody@nowhere.com";
	private static final long _42L = 42L;
	private TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private WorkRequestRepository workRequestRepository = Mockito.mock(WorkRequestRepository.class);

	private WorkRequestController workRequestController;

	@BeforeEach
	void setup() {
		this.workRequestController = new WorkRequestController(tidyUserRepository, workRequestRepository);
	}
	
	private static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";
	private WorkRequest workRequest(TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setId(_42L);
		request.setUser(user);
		return request;
	}

	@Test
	void getForUser() {
		when(tidyUserRepository.findById(_42L)).thenReturn(user());

		ResponseEntity<WorkRequestResource> response = workRequestController.get(_42L,user().get().getWorkRequests().iterator().next().getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	void getAllForUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		ResponseEntity<Resources<WorkRequestResource>> response = workRequestController.all(_42L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	private Role role() {
		Role role = new Role();
		role.setName(roleDto().toString());
		role.setId(_42L);
		return role;
	}

	private RoleDto roleDto() {
		return RoleDto.CUSTOMER;
	}

	private Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setId(_42L);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<Role>(Arrays.asList(role())));
		user.setWorkRequests(new ArrayList<WorkRequest>(Arrays.asList(workRequest(user))));
		return Optional.of(user);
	}

}
