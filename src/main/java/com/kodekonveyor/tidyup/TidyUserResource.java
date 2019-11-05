package com.kodekonveyor.tidyup;

import org.springframework.hateoas.ResourceSupport;

import lombok.NoArgsConstructor;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@NoArgsConstructor
public class TidyUserResource extends ResourceSupport {
	public TidyUserResource(final TidyUser user) {
		final long id = user.getId();
		add(linkTo(TidyUserController.class).withRel("all-users"));
		add(linkTo(methodOn(WorkRequestsController.class).all(id)).withRel("user-workrequests"));
		add(linkTo(methodOn(TidyUserController.class).get(id)).withSelfRel());
	}
}