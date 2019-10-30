package com.kodekonveyor.tidyup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/users", produces = "application/hal+json")
public class TidyUserController {

	@Autowired
	TidyUserRepository tidyUserRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public TidyUserController(TidyUserRepository repo) {
		this.tidyUserRepository = repo;
	}

	@GetMapping
	public ResponseEntity<Resources<TidyUserResource>> all() {
		final List<TidyUserResource> collection = tidyUserRepository.findAll().stream().map(TidyUserResource::new)
				.collect(Collectors.toList());
		final Resources<TidyUserResource> resources = new Resources<>(collection);
		final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
		resources.add(new Link(uriString, "self"));
		return ResponseEntity.ok(resources);
	}

	@GetMapping("/{id}")
	public ResponseEntity<TidyUserResource> get(@PathVariable final long id) {
		return tidyUserRepository.findById(id).map(p -> ResponseEntity.ok(new TidyUserResource(p)))
				.orElseThrow(() -> new TidyUserNotFoundException(id));
	}

	@PostMapping
	public ResponseEntity<TidyUserResource> post(@Valid @RequestBody final TidyUserDto tidyUserFromRequest) {
		Optional<TidyUser> already = tidyUserRepository.findByEmail(tidyUserFromRequest.getEmail());
		if (already.isPresent())
			throw new TidyUserAlreadyRegisteredException(already.get().getEmail());

		Role role = roleRepository.findByName(tidyUserFromRequest.getRole().toString());
		TidyUser u = new TidyUser();
		u.setEmail(tidyUserFromRequest.getEmail());
		u.setPassword(passwordEncoder.encode(tidyUserFromRequest.getPassword()));
		u.setRoles(new ArrayList<Role>(Arrays.asList(role)));
		u.setWorkRequests(new ArrayList<WorkRequest>());

		final TidyUser user = tidyUserRepository.save(u);
		final URI uri = MvcUriComponentsBuilder.fromController(getClass()).path("/{id}").buildAndExpand(user.getId())
				.toUri();
		return ResponseEntity.created(uri).body(new TidyUserResource(user));
	}

	@PutMapping("/{id}")
	public ResponseEntity<TidyUserResource> put(@PathVariable("id") final long id,
			@RequestBody TidyUserDto tidyUserFromRequest) {
		Role role = roleRepository.findByName(tidyUserFromRequest.getRole().toString());
		TidyUser u = new TidyUser();
		u.setEmail(tidyUserFromRequest.getEmail());
		u.setPassword(passwordEncoder.encode(tidyUserFromRequest.getPassword()));
		u.setRoles(new ArrayList<Role>(Arrays.asList(role)));
		u.setWorkRequests(new ArrayList<WorkRequest>());
		u.setId(id);

		final TidyUser person = tidyUserRepository.save(u);

		final TidyUserResource resource = new TidyUserResource(person);
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		return ResponseEntity.created(uri).body(resource);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") final long id) {
		return tidyUserRepository.findById(id).map(p -> {
			tidyUserRepository.deleteById(id);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new TidyUserNotFoundException(id));
	}
}
