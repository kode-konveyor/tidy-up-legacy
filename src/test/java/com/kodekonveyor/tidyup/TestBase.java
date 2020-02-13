package com.kodekonveyor.tidyup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public class TestBase {
	private static final String USER_PASSWORD = "userpassword";
	protected static final long USER_IDENTIFIER = 42L;
	private static final String USER_PASSWORD_ENCODED = "userpasswordencoded";
	protected static final String USER_EMAIL = "nobody@nowhere.com";
	protected static final long OTHER_USER_IDENTIFIER = 41L;
	protected static final String CITY = "CITY";
	private static final String REQUEST_DESCRIPTION = "want my space to be tidy";

	protected RoleDto roleDto() {
		return RoleDto.CUSTOMER_ROLE;
	}

	protected UserRole role() {
		UserRole role = new UserRole();
		role.setName(roleDto().toString());
		role.setIdentifier(USER_IDENTIFIER);
		
		Privilege privilege = new Privilege();
		privilege.setIdentifier(44L);
		privilege.setName("PRIVILEGE");
		privilege.setRoles(new ArrayList<UserRole>(Arrays.asList(role)));
		role.setPrivileges(new ArrayList<Privilege>(Arrays.asList(privilege)));

		return role;
	}

	private WorkRequest workRequest(final TidyUser user) {
		WorkRequest request = new WorkRequest();
		request.setCity(CITY);
		request.setDescription(REQUEST_DESCRIPTION);
		request.setIdentifier(USER_IDENTIFIER);
		request.setUser(user);
		return request;
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

	protected WorkRequestDto requestDto() {
		WorkRequestDto dto = new WorkRequestDto();
		dto.setCity(CITY);
		dto.setDescription(REQUEST_DESCRIPTION);

		return dto;
	}

	protected TidyUserDto userdto() {
		TidyUserDto dto = new TidyUserDto();
		dto.setEmail(USER_EMAIL);
		dto.setPassword(USER_PASSWORD);
		dto.setRole(roleDto());
		return dto;
	}

}
