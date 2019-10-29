package com.kodekonveyor.tidyup;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

public class RootResource extends ResourceSupport {
  public RootResource() {
    add(linkTo(TidyUserController.class).withRel("all-users"));
    add(linkTo(methodOn(RootController.class).root()).withSelfRel());
  }
}