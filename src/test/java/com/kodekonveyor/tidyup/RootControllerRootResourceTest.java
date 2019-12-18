package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

public class RootControllerRootResourceTest {
    @Test
    public void call() {
        RootController controller = new RootController();
        assertThat(controller.root().getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
