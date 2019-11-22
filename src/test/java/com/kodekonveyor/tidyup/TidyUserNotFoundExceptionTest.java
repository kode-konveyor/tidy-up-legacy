package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserNotFoundExceptionTest extends TestBase {
    @Test
    public void call() {
        final long identifier = 4_042_582_747_892_645_032L;
        TidyUserNotFoundException exception = new TidyUserNotFoundException(identifier);
        assertThat(exception).isNotEqualTo(null);
    }
}
