package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserTestRegisteringNewUser extends TidyUserTestBase {
	private TidyUserResource get() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
		when(roleRepository.findByName(userdto().getRole().toString())).thenReturn(role());
		when(passwordEncoder.encode(Mockito.any())).thenReturn(user().get().getPassword());
		when(tidyUserRepository.save(Mockito.any())).thenAnswer(
				invocation ->
				{
					TidyUser user = invocation.getArgumentAt(0, TidyUser.class);
					if (user.getEmail().equals(userdto().getEmail())
							&& user.getWorkRequests().isEmpty()
							&& user.getPassword().equals(user().get().getPassword())
							&& user.getRoles().size() == 1)
						return user().get();
					else
						throw new IllegalArgumentException();
				});

		return tidyUserController.post(userdto()).getBody();
	}

	@Test
	public void email() {
		assertThat(get()).isEqualTo(new TidyUserResource(user().get()));
	}
}
