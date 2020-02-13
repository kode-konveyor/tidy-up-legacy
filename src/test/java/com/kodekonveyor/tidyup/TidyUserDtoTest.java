package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserDtoTest extends TestBase {
    @Test
    public void call() {
        TidyUserDto dto = new TidyUserDto();
        assertThat(dto).isNotEqualTo(null);
    }
}
