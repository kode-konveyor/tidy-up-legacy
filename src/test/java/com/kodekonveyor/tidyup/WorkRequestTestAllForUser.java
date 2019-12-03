package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestTestAllForUser extends WorkRequestTestBase {
	private WorkRequestResource get() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		return workRequestController.all(USER_IDENTIFIER).getBody().getContent().iterator().next();

	}

	@Test
	public void hasSelfLink() {
		assertThat(get().getLink("self").getRel()).isEqualTo("self");
	}

	@Test
	public void hasOwnerLink() {
		assertThat(get().getLink("owner").getRel()).isEqualTo("owner");
	}

	@Test
	public void hasAllOwnerRequest() {
		assertThat(get().getLink("all-owner-requests").getRel()).isEqualTo("all-owner-requests");
	}

	@Test
	public void hasAllFromCity() {
		assertThat(get().getLink("all-requests-from-city").getRel()).isEqualTo("all-requests-from-city");
	}

	@Test
	public void hasRequestId() {
		assertThat(get().getLink("request-id").getRel()).isEqualTo("request-id");
	}
}
