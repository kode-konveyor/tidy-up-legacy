package com.kodekonveyor.tidyup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.Getter;

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	@Getter
	private static final String ADMIN_ROLE = "ADMIN";

	@Getter
	private static final String ADMIN_PRIVILEGE = "ADMIN_PRIVILEGE";

	private static final String USER_PRIVILEGE = "USER_PRIVILEGE";

	@Getter
	private static final String WORKER_PRIVILEGE = "WORKER_PRIVILEGE";

	@Getter
	private static final String CUSTOMER_PRIVILEGE = "CUSTOMER_PRIVILEGE";

	@Getter
	private static final String ADMIN_TEST_COM = "admin@test.com";

	@Getter
	private static final String CUSTOMER_TEST_COM = "customer@test.com";

	@Getter
	private static final String WORKER_TEST_COM = "worker@test.com";

	@Getter
	private boolean alreadySetup;

	@Autowired
	private final TidyUserRepository userRepository;

	@Autowired
	private final RoleRepository roleRepository;

	@Autowired
	private final PrivilegeRepository privilegeRepository;

	@Autowired
	private final PasswordEncoder passwordEncoder;

	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		if (!this.alreadySetup) {
			// == create initial privileges
			final Privilege customerPrivilege = createPrivilegeIfNotFound(CUSTOMER_PRIVILEGE);
			final Privilege workerPrivilege = createPrivilegeIfNotFound(WORKER_PRIVILEGE);
			final Privilege genericUserPrivilege = createPrivilegeIfNotFound(USER_PRIVILEGE);
			final Privilege adminPrivilege = createPrivilegeIfNotFound(ADMIN_PRIVILEGE);

			// == create initial roles
			final List<Privilege> customerPrivileges = new ArrayList<>(
					Arrays.asList(customerPrivilege, genericUserPrivilege));
			final List<Privilege> workerPrivileges = new ArrayList<>(
					Arrays.asList(workerPrivilege, genericUserPrivilege));
			final List<Privilege> adminPrivileges = new ArrayList<>(
					Arrays.asList(workerPrivilege, customerPrivilege, genericUserPrivilege, adminPrivilege));
			final UserRole customerRole = createRoleIfNotFound(RoleDto.CUSTOMER_ROLE.toString(), customerPrivileges);
			final UserRole workerRole = createRoleIfNotFound(RoleDto.WORKER_ROLE.toString(), workerPrivileges);
			final UserRole adminRole = createRoleIfNotFound(ADMIN_ROLE, adminPrivileges);

			// == create initial user
			createUserIfNotFound(WORKER_TEST_COM, "test", new ArrayList<UserRole>(Arrays.asList(workerRole)));
			createUserIfNotFound(CUSTOMER_TEST_COM, "test", new ArrayList<UserRole>(Arrays.asList(customerRole)));
			createUserIfNotFound(ADMIN_TEST_COM, "test", new ArrayList<UserRole>(Arrays.asList(adminRole)));

			this.alreadySetup = true;
		}
	}

	@Transactional
	private  Privilege createPrivilegeIfNotFound(final String name) {
		Privilege privilege = privilegeRepository.findByName(name);
		if (privilege == null) {
			privilege = new Privilege();
			privilege.setName(name);
			privilege = privilegeRepository.save(privilege);
		}
		return privilege;
	}

	@Transactional
	private UserRole createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
		UserRole role = roleRepository.findByName(name);
		if (role == null) {
			role = new UserRole();
			role.setName(name);
		}
		role.setPrivileges(privileges);
		role = roleRepository.save(role);
		return role;
	}

	@Transactional
	private void createUserIfNotFound(final String email, final String password,
			final Collection<UserRole> roles) {
		TidyUser user = userRepository
				.findByEmail(email)
				.map(u -> u)
				.orElseGet(() -> {
			TidyUser newUser = new TidyUser();
			newUser.setPassword(passwordEncoder.encode(password));
			newUser.setEmail(email);
			return newUser;
		});

		user.setRoles(roles);
		userRepository.save(user);
	}

	public SetupDataLoader(final TidyUserRepository userRepository, final RoleRepository roleRepository,
			final PrivilegeRepository privilegeRepository, final PasswordEncoder passwordEncoder) {
		super();
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.privilegeRepository = privilegeRepository;
		this.passwordEncoder = passwordEncoder;
	}
}
