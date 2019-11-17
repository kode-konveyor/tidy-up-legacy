package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class PrincipalReturningControllerGetPrincipal {
    @Test
    public void call() {
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("hello");
        SecurityContextHolder.setContext(securityContext);

        PrincipalReturningController controller = new PrincipalReturningController();
        assertThat(controller.getPrincipal()).isEqualTo("hello");
    }
}
