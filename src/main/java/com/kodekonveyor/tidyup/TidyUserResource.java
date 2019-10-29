package com.kodekonveyor.tidyup;

import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Getter
public class TidyUserResource extends ResourceSupport {

  private final TidyUser user;

  public TidyUserResource(final TidyUser user) {
    this.user = user;
    final long id = user.getId();
    add(linkTo(TidyUserController.class).withRel("all-users"));
    add(linkTo(methodOn(WorkRequestsController.class).all(id)).withRel("user-requests"));
    add(linkTo(methodOn(TidyUserController.class).get(id)).withSelfRel());
  }
}