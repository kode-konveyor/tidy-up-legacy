package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class WorkRequestTestForUserOk extends WorkRequestTestBase {
	@Test
	public void call() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		ResponseEntity<WorkRequestResource> response = workRequestController.get(USER_IDENTIFIER,
				user().get().getWorkRequests().iterator().next().getIdentifier());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
}
