package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserAlreadyRegisteredExceptionTest extends TestBase {
    @Test
    public void call() {
        final String email = "hello@test.com";
        TidyUserAlreadyRegisteredException exception = new TidyUserAlreadyRegisteredException(email);
        assertThat(exception).isNotEqualTo(null);
    }
}
