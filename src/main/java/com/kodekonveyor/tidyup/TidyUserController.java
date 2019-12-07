package com.kodekonveyor.tidyup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
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

	private static final String SLASH_ID_PARAMETER = "/{id}";

	@Autowired
	private final TidyUserRepository tidyUserRepository;

	@Autowired
	private final RoleRepository roleRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	public TidyUserController(final TidyUserRepository tidyUserRepository, final RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {
		this.tidyUserRepository = tidyUserRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public ResponseEntity<Resources<TidyUserResource>> all() {
		final List<TidyUserResource> collection = tidyUserRepository.findAll().stream().map(TidyUserResource::new)
				.collect(Collectors.toList());
		final Resources<TidyUserResource> resources = new Resources<>(collection);
		return ResponseEntity.ok(resources);
	}

	@GetMapping(SLASH_ID_PARAMETER)
	public ResponseEntity<TidyUserResource> get(@PathVariable final long identifier) {
		return tidyUserRepository
				.findById(identifier)
				.map(p -> ResponseEntity.ok(new TidyUserResource(p)))
				.orElseThrow(() -> new TidyUserNotFoundException(identifier));
	}

	@PostMapping
	public ResponseEntity<TidyUserResource> post(@Valid @RequestBody final TidyUserDto tidyUserFromRequest) {
		Optional<TidyUser> already = tidyUserRepository.findByEmail(tidyUserFromRequest.getEmail());
		if (already.isPresent())
			throw new TidyUserAlreadyRegisteredException(already.get().getEmail());

		UserRole role = roleRepository.findByName(tidyUserFromRequest.getRole().toString());
		TidyUser userToRegister = new TidyUser();
		userToRegister.setEmail(tidyUserFromRequest.getEmail());
		userToRegister.setPassword(passwordEncoder.encode(tidyUserFromRequest.getPassword()));
		userToRegister.setRoles(new ArrayList<UserRole>(Arrays.asList(role)));
		userToRegister.setWorkRequests(new ArrayList<WorkRequest>());

		final TidyUser user = tidyUserRepository.save(userToRegister);
		final Long identifier = user.getIdentifier();
		final URI uri = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path(SLASH_ID_PARAMETER)
				.buildAndExpand(identifier)
				.toUri();
		return ResponseEntity.created(uri).body(new TidyUserResource(user));
	}

	@PutMapping(SLASH_ID_PARAMETER)
	public ResponseEntity<TidyUserResource> put(@PathVariable("id") final long identifier,
			@RequestBody final TidyUserDto tidyUserFromRequest) {
		UserRole role = roleRepository.findByName(tidyUserFromRequest.getRole().toString());
		TidyUser user = new TidyUser();
		user.setEmail(tidyUserFromRequest.getEmail());
		user.setPassword(passwordEncoder.encode(tidyUserFromRequest.getPassword()));
		user.setRoles(new ArrayList<UserRole>(Arrays.asList(role)));
		user.setWorkRequests(new ArrayList<WorkRequest>());
		user.setIdentifier(identifier);

		final TidyUser person = tidyUserRepository.save(user);

		final TidyUserResource resource = new TidyUserResource(person);
		final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();
		return ResponseEntity.created(uri).body(resource);
	}

	@DeleteMapping(SLASH_ID_PARAMETER)
	public ResponseEntity<?> delete(@PathVariable("id") final long identifier) {
		return tidyUserRepository.findById(identifier).map(p -> {
			tidyUserRepository.deleteById(identifier);
			return ResponseEntity.noContent().build();
		}).orElseThrow(() -> new TidyUserNotFoundException(identifier));
	}
}
