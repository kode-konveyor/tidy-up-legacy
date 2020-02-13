package com.kodekonveyor.tidyup;

import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class RootResource extends ResourceSupport {
	private static final String ALL_USERS = "all-users";

	public RootResource() {
		super();
		add(linkTo(TidyUserController.class).withRel(ALL_USERS));
		add(linkTo(methodOn(RootController.class).root()).withSelfRel());
	}
}