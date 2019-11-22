package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkRequestDtoTest {

	@Test
	public void call() {
		assertThat(new WorkRequestDto()).isNotNull();
	}

}
