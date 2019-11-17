package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserResourceTest extends TestBase {
    @Test
    public void call() {
        TidyUserResource resource = new TidyUserResource(user().get());
    }
}
