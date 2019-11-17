package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class RootControllerRootResource {
    @Test
    public void call() {
        RootController controller = new RootController();
        assertThat(controller.root().getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
