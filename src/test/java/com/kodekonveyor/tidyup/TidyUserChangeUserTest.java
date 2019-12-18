package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserChangeUserTest extends TidyUserTestBase {
	private TidyUserResource resource;

	@BeforeEach
    @Override
	public void setUp() {
		super.setUp();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(roleRepository.findByName(userdto().getRole().toString())).thenReturn(role());
		when(passwordEncoder.encode(Mockito.any())).thenReturn(user().get().getPassword());
		when(tidyUserRepository.save(Mockito.any())).thenAnswer(
				invocation ->
				{
					TidyUser user = invocation.getArgumentAt(0, TidyUser.class);
					if (user.getEmail().equals(userdto().getEmail())
							&& user.getIdentifier().equals(user().get().getIdentifier())
							&& user.getWorkRequests().isEmpty()
							&& user.getPassword().equals(user().get().getPassword())
							&& user.getRoles().size() == 1)
						return user().get();
					else
						throw new IllegalArgumentException();
				});

		this.resource = this.tidyUserController.put(user().get().getIdentifier(), userdto()).getBody();
	}

	@Test
	public void email() {
		assertThat(this.resource.getLink("self").getRel()).isEqualTo("self");
	}
}
