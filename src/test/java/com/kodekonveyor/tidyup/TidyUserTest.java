package com.kodekonveyor.tidyup;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class TidyUserTest {
	private static final String USER_PASSWORD = "userpassword";
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	private static final String USER_EMAIL = "nobody@nowhere.com";
	private static final long USER_IDENTIFIER = 42L;
	private final TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	private TidyUserController tidyUserController;

	@BeforeEach
	void setup() {
		this.tidyUserController = new TidyUserController(tidyUserRepository, roleRepository, passwordEncoder);
	}
	
	private static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";
	private WorkRequest workRequest(TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setIdentifier(USER_IDENTIFIER);
		request.setUser(user);
		return request;
	}

	@Test
	void registeredUserGetsItself() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		ResponseEntity<TidyUserResource> response = tidyUserController.get(USER_IDENTIFIER);

		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));

	}
	
	@Test
	void notRegisteredUserGetFails() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { tidyUserController.get(USER_IDENTIFIER); });
	}
	
	@Test
	void registeringNewUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(Optional.empty());
		when(roleRepository.findByName(userdto().getRole().toString())).thenReturn(role());
		when(tidyUserRepository.save(Mockito.any())).thenReturn(user().get());
		
		ResponseEntity<TidyUserResource> response = tidyUserController.post(userdto());
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
	}

	@Test
	void alreadyRegisteredUser() {
		when(tidyUserRepository.findByEmail(userdto().getEmail())).thenReturn(user());
		assertThrows(TidyUserAlreadyRegisteredException.class, () -> { tidyUserController.post(userdto()); });
	}
	
	@Test
	void changeUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
		when(roleRepository.findByName(roleDto().toString())).thenReturn(role());
		when(tidyUserRepository.save(Mockito.any())).thenReturn(user().get());
		ResponseEntity<TidyUserResource> response = tidyUserController.put(USER_IDENTIFIER,userdto());
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	void delete() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		
		ResponseEntity<?> response = tidyUserController.delete(USER_IDENTIFIER);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	void deleteMissing() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		
		assertThrows(TidyUserNotFoundException.class, () -> { tidyUserController.delete(USER_IDENTIFIER); });
	}
	
	@Test
	void getAll() {
		when(tidyUserRepository.findAll()).thenReturn(new ArrayList<TidyUser>(Arrays.asList(user().get())));
		ResponseEntity<Resources<TidyUserResource>> response = tidyUserController.all();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().getContent().size()).isEqualTo(1);
	}
	
	
	private Role role() {
		Role role = new Role();
		role.setName(roleDto().toString());
		role.setIdentifier(USER_IDENTIFIER);
		return role;
	}

	private RoleDto roleDto() {
		return RoleDto.CUSTOMER;
	}

	private TidyUserDto userdto() {
		TidyUserDto dto = new TidyUserDto();
		dto.setEmail(USER_EMAIL);
		dto.setPassword(USER_PASSWORD);
		dto.setRole(roleDto());
		return dto;
	}

	private Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setIdentifier(USER_IDENTIFIER);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<Role>(Arrays.asList(role())));
		user.setWorkRequests(new ArrayList<WorkRequest>(Arrays.asList(workRequest(user))));
		return Optional.of(user);
	}

}
