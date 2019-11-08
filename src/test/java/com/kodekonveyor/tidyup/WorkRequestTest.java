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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class WorkRequestTest {
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	private static final String USER_EMAIL = "nobody@nowhere.com";
	private static final long USER_IDENTIFIER = 42L;
	private static final long OTHER_USER_IDENTIFIER = 41L;
	private static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";
	private final TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	private final WorkRequestRepository workRequestRepository = Mockito.mock(WorkRequestRepository.class);

	private WorkRequestController workRequestController;

	@BeforeEach
	public void setUp() {
		this.workRequestController = new WorkRequestController(tidyUserRepository, workRequestRepository);
	}
	
	private WorkRequest workRequest(final TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setIdentifier(USER_IDENTIFIER);
		request.setUser(user);
		return request;
	}

	@Test
	public void forUserOk() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		ResponseEntity<WorkRequestResource> response = workRequestController.get(USER_IDENTIFIER,user().get().getWorkRequests().iterator().next().getIdentifier());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void forUserNotFound() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		assertThrows(WorkRequestNotFoundException.class, () -> { workRequestController.get(USER_IDENTIFIER,user().get().getWorkRequests().iterator().next().getIdentifier()+1L); });
	}
	
	@Test
	public void forNonExistentUser() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.get(USER_IDENTIFIER,user().get().getWorkRequests().iterator().next().getIdentifier()); });
	}

	@Test
	public void nonExistentForUser() {
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.get(USER_IDENTIFIER,user().get().getWorkRequests().iterator().next().getIdentifier()+1L); });
	}
	
	@Test
	public void allForUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		ResponseEntity<Resources<WorkRequestResource>> response = workRequestController.all(USER_IDENTIFIER);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	@Test
	public void allForNonExistentUser() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.all(USER_IDENTIFIER); });
	}
	
	@Test
	public void allForCity() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(workRequestRepository.findAllByCity(CITY)).thenReturn(new ArrayList<WorkRequest>());
        ResponseEntity<Resources<WorkRequestResource>> response = workRequestController.city(CITY);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}
	
	
	@Test
	public void createUserNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.post(USER_IDENTIFIER, requestDto()); });
	}
	
	@Test
	public void createOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseEntity<WorkRequestResource> response = workRequestController.post(USER_IDENTIFIER, requestDto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	public void createForbidden() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseEntity<WorkRequestResource> responseOther = workRequestController.post(OTHER_USER_IDENTIFIER, requestDto());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	public void putOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		
		ResponseEntity<WorkRequestResource> response = workRequestController.put(USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier(), requestDto());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}
	
	@Test
	public void putForbidden() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseEntity<WorkRequestResource> responseOther = workRequestController.put(OTHER_USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier(), requestDto());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
	
	@Test
	public void putUserNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		
		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.put(USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier(), requestDto()); });
	}

	@Test
	public void deleteOk() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		ResponseEntity<?> response = workRequestController.delete(USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test
	public void deleteForbidden() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		
		ResponseEntity<?> responseOther = workRequestController.delete(OTHER_USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier());
		assertThat(responseOther.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}
	
	@Test
	public void deleteWorkRequestNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		assertThrows(WorkRequestNotFoundException.class, () -> { workRequestController.delete(USER_IDENTIFIER,user().get().getWorkRequests().iterator().next().getIdentifier()+1L); });
	}
	
	@Test
	public void deleteUserNotFound() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(user());
		when(tidyUserRepository.findByEmail(USER_EMAIL)).thenReturn(user());
		when(workRequestRepository.save(Mockito.any())).thenReturn(user().get().getWorkRequests().iterator().next());

		User user = new User(USER_EMAIL,"",true,true,true,false,new ArrayList<GrantedAuthority>());
		
		Authentication authentication = Mockito.mock(Authentication.class);
		when(authentication.getPrincipal()).thenReturn(user);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);

		when(tidyUserRepository.findById(USER_IDENTIFIER)).thenReturn(Optional.empty());
		assertThrows(TidyUserNotFoundException.class, () -> { workRequestController.delete(USER_IDENTIFIER, user().get().getWorkRequests().iterator().next().getIdentifier()); });
	}

	
	private UserRole role() {
		UserRole role = new UserRole();
		role.setName(roleDto().toString());
		role.setIdentifier(USER_IDENTIFIER);
		return role;
	}

	private RoleDto roleDto() {
		return RoleDto.CUSTOMER;
	}

	private Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setIdentifier(USER_IDENTIFIER);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<UserRole>(Arrays.asList(role())));
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
