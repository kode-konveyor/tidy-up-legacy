package com.kodekonveyor.tidyup;

import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping(value = "", produces = "application/hal+json")
public class WorkRequestController {

	// private static final String SELF = "self";

	@Autowired
	private final TidyUserRepository tidyUserRepository;

	@Autowired
	private final WorkRequestRepository workRequestRepository;
	
	public WorkRequestController (final TidyUserRepository tidyUserRepository, final WorkRequestRepository workRequestRepository) {
		this.tidyUserRepository = tidyUserRepository;
		this.workRequestRepository = workRequestRepository;
	}

	@GetMapping("/users/{userId}/workrequests")
	public ResponseEntity<Resources<WorkRequestResource>> all(@PathVariable final long userId) {
		final List<WorkRequestResource> collection = getWorkRequestsForUser(userId);
		final Resources<WorkRequestResource> resources = new Resources<>(collection);
		// final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
		// resources.add(new Link(uriString, SELF));
		return ResponseEntity.ok(resources);
	}

	private List<WorkRequestResource> getWorkRequestsForUser(final long userId) {
		return tidyUserRepository
				.findById(userId)
				.map(
						p -> p
						.getWorkRequests()
						.stream()
						.map(WorkRequestResource::new)
						.collect(Collectors.toList()))
				.orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

	private List<WorkRequestResource> getWorkRequestsForCity(final String city) {
		return workRequestRepository.findAllByCity(city).stream().map(WorkRequestResource::new)
				.collect(Collectors.toList());
	}

	@GetMapping("/users/{userId}/workrequests/{workRequestId}")
	public ResponseEntity<WorkRequestResource> get(@PathVariable final long userId,
			@PathVariable final long workRequestId) {
		return tidyUserRepository.findById(userId)
				.map(
						p -> p
						.getWorkRequests()
						.stream()
						.filter(m -> m.getIdentifier().equals(workRequestId))
						.findAny()
						.map(m -> ResponseEntity.ok(new WorkRequestResource(m)))
						.orElseThrow(() -> new WorkRequestNotFoundException(workRequestId)))
				.orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

	@PostMapping("/users/{userId}/workrequests")
	public ResponseEntity<WorkRequestResource> post(@PathVariable final long userId,
			@RequestBody final WorkRequestDto inputRequest) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (tidyUserRepository.findByEmail(user.getUsername()).get().getIdentifier() != userId)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

		return tidyUserRepository.findById(userId).map(p -> {
			WorkRequest requestToSave = new WorkRequest();
			requestToSave.setCity(inputRequest.getCity());
			requestToSave.setDescription(inputRequest.getDescription());
			requestToSave.setUser(p);
			WorkRequest request = workRequestRepository.save(requestToSave);
			final URI uri = createPostUri(request);
			Objects.requireNonNull(uri);
			return ResponseEntity.created(uri).body(new WorkRequestResource(request));
		}).orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

	private URI createPostUri(final WorkRequest workRequest) {
		return ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{workRequestId}")
				.buildAndExpand(workRequest.getUser().getIdentifier(), workRequest.getIdentifier())
				.toUri();
	}

	@PutMapping("/users/{userId}/workrequests/{workRequestId}")
	public ResponseEntity<WorkRequestResource> put(@PathVariable final long userId,
			@PathVariable final long workRequestId, @RequestBody final WorkRequestDto inputRequest) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (tidyUserRepository.findByEmail(user.getUsername()).get().getIdentifier() != userId)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		return tidyUserRepository.findById(userId).map(p -> {
			WorkRequest requestToSave = new WorkRequest();
			requestToSave.setCity(inputRequest.getCity());
			requestToSave.setDescription(inputRequest.getDescription());
			requestToSave.setUser(p);
			requestToSave.setIdentifier(workRequestId);
			WorkRequest request = workRequestRepository.save(requestToSave);
			final URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentRequest().toUriString());
			return ResponseEntity.created(uri).body(new WorkRequestResource(request));
		}).orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

	@DeleteMapping("/users/{userId}/workrequests/{workRequestId}")
	public ResponseEntity<?> delete(@PathVariable final long userId, @PathVariable final long workRequestId) {
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (tidyUserRepository.findByEmail(user.getUsername()).get().getIdentifier() != userId)
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		return tidyUserRepository.findById(userId)
				.map(p -> p.getWorkRequests().stream().filter(m -> m.getIdentifier().equals(workRequestId)).findAny().map(m -> {
					workRequestRepository.delete(m);
					return ResponseEntity.noContent().build();
				}).orElseThrow(() -> new WorkRequestNotFoundException(workRequestId)))
				.orElseThrow(() -> new TidyUserNotFoundException(userId));
	}

	@GetMapping("/workrequests/{city}")
	public ResponseEntity<Resources<WorkRequestResource>> city(@PathVariable final String city) {
		final List<WorkRequestResource> collection = getWorkRequestsForCity(city);
		final Resources<WorkRequestResource> resources = new Resources<>(collection);
		// final String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
		// resources.add(new Link(uriString, SELF));
		return ResponseEntity.ok(resources);
	}
}
