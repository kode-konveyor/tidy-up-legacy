package com.kodekonveyor.tidyup;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestTest {
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	private static final String USER_EMAIL = "nobody@nowhere.com";
	private static final long _42L = 42L;
	private static final long _41L = 41L;
	private TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private WorkRequestRepository workRequestRepository = Mockito.mock(WorkRequestRepository.class);

	private WorkRequestController workRequestController;

	@BeforeEach
	void setup() {
		this.workRequestController = new WorkRequestController(tidyUserRepository, workRequestRepository);
	}
	
	private static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";
	private WorkRequest workRequest(TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setId(_42L);
		request.setUser(user);
		return request;
	}

	@Test
	void getForUser() {
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		ResponseEntity<WorkRequestResource> response = workRequestController.get(_42L,user().get().getWorkRequests().iterator().next().getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThrows(WorkRequestNotFoundException.class, () -> { workRequestController.get(_42L,user().get().getWorkRequests().iterator().next().getId()+1L); });
	}
	
	@Test
	void getForNonExistentUser() {
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.get(_42L,user().get().getWorkRequests().iterator().next().getId()); });
	}

	@Test
	void getNonExistentForUser() {
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.get(_42L,user().get().getWorkRequests().iterator().next().getId()+1L); });
	}
	
	@Test
	void getAllForUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		ResponseEntity<Resources<WorkRequestResource>> response = workRequestController.all(_42L);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	void getAllForNonExistentUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.all(_42L); });
	}
	
	@Test
	void getAllForCity() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(workRequestRepository.findAllByCity(CITY)).thenReturn(new ArrayList<WorkRequest>());
        ResponseEntity<Resources<WorkRequestResource>> response = workRequestController.city(CITY);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	
	@Test
	void create() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseEntity<WorkRequestResource> response = workRequestController.post(_42L, requestDto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ResponseEntity<WorkRequestResource> responseOther = workRequestController.post(_41L, requestDto());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.post(_42L, requestDto()); });
	}

	@Test
	void put() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		
		ResponseEntity<WorkRequestResource> response = workRequestController.put(_42L, user().get().getWorkRequests().iterator().next().getId(), requestDto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		
		ResponseEntity<WorkRequestResource> responseOther = workRequestController.put(_41L, user().get().getWorkRequests().iterator().next().getId(), requestDto());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.put(_42L, user().get().getWorkRequests().iterator().next().getId(), requestDto()); });
	}

	@Test
	void delete() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(_42L)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		
		ResponseEntity<?> response = workRequestController.delete(_42L, user().get().getWorkRequests().iterator().next().getId());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		
		ResponseEntity<?> responseOther = workRequestController.delete(_41L, user().get().getWorkRequests().iterator().next().getId());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		assertThrows(WorkRequestNotFoundException.class, () -> { workRequestController.delete(_42L,user().get().getWorkRequests().iterator().next().getId()+1L); });
		
		when(tidyUserRepository.findById(_42L)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.delete(_42L, user().get().getWorkRequests().iterator().next().getId()); });
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

	private Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setId(_42L);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<Role>(Arrays.asList(role())));
		user.setWorkRequests(new ArrayList<WorkRequest>(Arrays.asList(workRequest(user))));
		return Optional.of(user);
	}
	
	private WorkRequestDto requestDto() {
		WorkRequestDto dto = new WorkRequestDto();
		dto.setCity(CITY);
		dto.setDescription(REQUEST_DESCRIPTION);
		
		return dto;
	}

}
