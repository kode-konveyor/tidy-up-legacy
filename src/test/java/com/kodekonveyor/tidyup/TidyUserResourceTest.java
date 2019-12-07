package com.kodekonveyor.tidyup;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TidyUserResourceTest extends TestBase {

    public static final String ALL_USERS = "all-users";

    private TidyUserResource get() {
        return new TidyUserResource(user().get());
    }

    @Test
    public void hasAllUsers() {
        assertThat(get().getLink(ALL_USERS).getRel()).isEqualTo(ALL_USERS);
    }

    @Test
    public void hasUserRequests() {
        assertThat(get().getLink("user-workrequests").getRel()).isEqualTo("user-workrequests");
    }

    @Test
    public void hasSelf() {
        assertThat(get().getLink("self").getRel()).isEqualTo("self");
    }

    @Test
    public void emptyConstructor() {
    	assertThat(new TidyUserResource().getLink(ALL_USERS).getRel()).isEqualTo(ALL_USERS);
    }
}
