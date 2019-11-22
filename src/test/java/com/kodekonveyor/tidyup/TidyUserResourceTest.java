package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserResourceTest extends TestBase {
    @Test
    public void call() {
        assertThat(new TidyUserResource(user().get())).isNotNull();
    }
    
    @Test
    public void call2()
    {
    	assertThat(new TidyUserResource()).isNotNull();
    }
}
