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
	
	private WorkRequest workRequest;
	
	  public WorkRequestResource(final WorkRequest workRequest) {
		    this.workRequest = workRequest;
		    final long id = workRequest.getId();
		    final long userId = workRequest.getUser().getId();
		    final String city = workRequest.getCity();
		    add(new Link(String.valueOf(id), "request-id"));
		    add(linkTo(methodOn(WorkRequestsController.class).all(userId)).withRel("all-owner-requests"));
		    add(linkTo(methodOn(TidyUserController.class).get(userId)).withRel("owner"));
		    add(linkTo(methodOn(WorkRequestsController.class).city(city)).withRel("all-requests-from-city"));
		    add(linkTo(methodOn(WorkRequestsController.class).get(userId, id)).withSelfRel());
		}

}
