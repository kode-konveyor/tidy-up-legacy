package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserAllTest extends TidyUserTestBase {
	private ResponseEntity<Resources<TidyUserResource>> returnedValue;
	private List<Link> links;
	@BeforeEach
	@Override
	public void setUp() {
		super.setUp();
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findAll()).thenReturn(new ArrayList<TidyUser>(Arrays.asList(user().get())));
		this.returnedValue = tidyUserController.all();
		this.links = returnedValue.getBody().getContent().iterator().next().getLinks();
	}

	@Test
	public void oneResource() {
		assertThat(this.returnedValue.getBody().getContent().size()).isEqualTo(1);
	}

	@Test
	public void threeLinks() {
		assertThat(this.links.size()).isEqualTo(3);
	}

	@Test
	public void hasSelf() {
		assertThat(this.returnedValue.getBody().getContent().iterator().next().getLink("self").getRel()).isEqualTo("self");
	}
}
