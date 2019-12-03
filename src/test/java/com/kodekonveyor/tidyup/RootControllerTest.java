package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RootControllerTest {

    private static final RootController ROOT_CONTROLLER = new RootController();

	@Test
	public void testSelf() {
		assertThat(ROOT_CONTROLLER.root().getBody().getLink("self").getRel()).isEqualTo("self");
	}

	@Test
	public void testAllUsers() {
		assertThat(ROOT_CONTROLLER.root().getBody().getLink("all-users").getRel()).isEqualTo("all-users");
	}
}
