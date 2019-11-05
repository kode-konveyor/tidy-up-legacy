package com.kodekonveyor.tidyup;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.eq;
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
	private static final long _42L = 42L;
	private TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	private PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	private TidyUserController tidyUserController;

	@BeforeEach
	void setup() {
		this.tidyUserController = new TidyUserController(tidyUserRepository, roleRepository, passwordEncoder);
	}

	@Test
	void registeredUserGetsItself() {
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		ResponseEntity<TidyUserResource> response = tidyUserController.get(_42L);

		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));

	}
	
	@Test
	void notRegisteredUserGetFails() {
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { tidyUserController.get(_42L); });
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
		ResponseEntity<TidyUserResource> response = tidyUserController.put(_42L,userdto());
		assertThat(response).isNotNull();
		assertThat(response.getBody()).isEqualTo(new TidyUserResource(user().get()));
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	void delete() {
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		
		ResponseEntity<?> response = tidyUserController.delete(_42L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	void deleteMissing() {
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		
		assertThrows(TidyUserNotFoundException.class, () -> { tidyUserController.delete(_42L); });
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
		role.setId(_42L);
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
		user.setId(_42L);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<Role>(Arrays.asList(role())));
		user.setWorkRequests(new ArrayList<WorkRequest>());
		return Optional.of(user);
	}

}
