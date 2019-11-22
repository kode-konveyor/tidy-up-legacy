package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RootControllerTest {

	@Test
	public void testRoot() {
		RootController controller = new RootController();
		assertThat(controller.root().getBody()).isExactlyInstanceOf(RootResource.class);
	}

}
