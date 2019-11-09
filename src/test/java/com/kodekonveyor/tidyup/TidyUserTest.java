package com.kodekonveyor.tidyup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TidyUserTest {
	private static final String USER_PASSWORD = "userpassword";
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	protected static final String USER_EMAIL = "nobody@nowhere.com";
	protected static final long USER_IDENTIFIER = 42L;
	private static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";
	protected final TidyUserRepository tidyUserRepository = Mockito.mock(TidyUserRepository.class);
	protected final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	protected final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	protected TidyUserController tidyUserController;

	@BeforeEach
	public void setUp() {
		this.tidyUserController = new TidyUserController(tidyUserRepository, roleRepository, passwordEncoder);
	}

	protected WorkRequest workRequest(final TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setIdentifier(USER_IDENTIFIER);
		request.setUser(user);
		return request;
	}

	protected UserRole role() {
		UserRole role = new UserRole();
		role.setName(roleDto().toString());
		role.setIdentifier(USER_IDENTIFIER);
		return role;
	}

	protected RoleDto roleDto() {
		return RoleDto.CUSTOMER;
	}

	protected Optional<TidyUser> user() {
		TidyUser user = new TidyUser();
		user.setIdentifier(USER_IDENTIFIER);
		user.setEmail(USER_EMAIL);
		user.setPassword(USER_PASSWORD_ENCODED);
		user.setRoles(new ArrayList<UserRole>(Arrays.asList(role())));
		user.setWorkRequests(new ArrayList<WorkRequest>(Arrays.asList(workRequest(user))));
		return Optional.of(user);
	}
	
	protected TidyUserDto userdto() {
		TidyUserDto dto = new TidyUserDto();
		dto.setEmail(USER_EMAIL);
		dto.setPassword(USER_PASSWORD);
		dto.setRole(roleDto());
		return dto;
	}
}
