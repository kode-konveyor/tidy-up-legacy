package com.kodekonveyor.tidyup;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
@NoArgsConstructor
public class WorkRequestResource extends ResourceSupport {

	private static final String ALL_REQUESTS_FROM_CITY = "all-requests-from-city";
	private static final String OWNER = "owner";
	private static final String ALL_OWNER_REQUESTS = "all-owner-requests";
	private static final String REQUEST_ID = "request-id";
	
	private WorkRequest workRequest;

	public WorkRequestResource(final WorkRequest workRequest) {
		super();
		this.workRequest = workRequest;
		final long identifier = workRequest.getIdentifier();
		final long userIdentifier = workRequest.getUser().getIdentifier();
		final String city = workRequest.getCity();
		add(new Link(String.valueOf(identifier), REQUEST_ID));
		add(linkTo(methodOn(WorkRequestController.class).all(userIdentifier)).withRel(ALL_OWNER_REQUESTS));
		add(linkTo(methodOn(TidyUserController.class).get(userIdentifier)).withRel(OWNER));
		add(linkTo(methodOn(WorkRequestController.class).city(city)).withRel(ALL_REQUESTS_FROM_CITY));
		add(linkTo(methodOn(WorkRequestController.class).get(userIdentifier, identifier)).withSelfRel());
	}

}
