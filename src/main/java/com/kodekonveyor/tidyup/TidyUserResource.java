package com.kodekonveyor.tidyup;

import org.springframework.hateoas.ResourceSupport;

// import lombok.Getter;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class TidyUserResource extends ResourceSupport {
	private static final String USER_WORKREQUESTS = "user-workrequests";
	
	// @Getter
	private static final String ALL_USERS = "all-users";
	
	public TidyUserResource() {
		super();
		add(linkTo(TidyUserController.class).withRel(ALL_USERS));
	}

	public TidyUserResource(final TidyUser user) {
		super();
		final long identifier = user.getIdentifier();
		add(linkTo(TidyUserController.class).withRel(ALL_USERS));
		add(linkTo(methodOn(WorkRequestController.class).all(identifier)).withRel(USER_WORKREQUESTS));
		add(linkTo(methodOn(TidyUserController.class).get(identifier)).withSelfRel());
	}
}