package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

public class WorkRequestControllerAdviceTest extends TestBase {
    @Test
    public void call() {
            WorkRequestNotFoundException exception = new WorkRequestNotFoundException(1L);
            ResponseEntity<VndErrors> response = new WorkRequestControllerAdvice().notFoundException(exception);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
