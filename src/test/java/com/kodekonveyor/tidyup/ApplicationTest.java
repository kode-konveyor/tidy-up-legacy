package com.kodekonveyor.tidyup;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ApplicationTest {

	@Test
	public void testMain() {
		assertDoesNotThrow(() -> { Application.main(new String[] {}); });
	}

}
