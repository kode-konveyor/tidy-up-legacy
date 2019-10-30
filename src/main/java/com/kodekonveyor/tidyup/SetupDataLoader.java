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

@Component
public class SetupDataLoader implements ApplicationListener<ContextRefreshedEvent> {
	private boolean alreadySetup = false;

    @Autowired
    private TidyUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

	
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
        // == create initial privileges
        // TODO
        final Privilege customerPrivilege = createPrivilegeIfNotFound("CUSTOMER_PRIVILEGE");
        final Privilege workerPrivilege = createPrivilegeIfNotFound("WORKER_PRIVILEGE");
        final Privilege genericUserPrivilege = createPrivilegeIfNotFound("USER_PRIVILEGE");
        final Privilege adminPrivilege = createPrivilegeIfNotFound("ADMIN_PRIVILEGE");

        // == create initial roles
        final List<Privilege> customerPrivileges = new ArrayList<Privilege>(Arrays.asList(customerPrivilege,genericUserPrivilege));
        final List<Privilege> workerPrivileges = new ArrayList<Privilege>(Arrays.asList(workerPrivilege,genericUserPrivilege));
        final List<Privilege> adminPrivileges = new ArrayList<Privilege>(Arrays.asList(workerPrivilege,customerPrivilege,genericUserPrivilege,adminPrivilege));
        final Role customerRole = createRoleIfNotFound(RoleDto.CUSTOMER.toString(), customerPrivileges);
        final Role workerRole = createRoleIfNotFound(RoleDto.WORKER.toString(), workerPrivileges);
        final Role adminRole = createRoleIfNotFound("ADMIN", adminPrivileges);

        // == create initial user
        createUserIfNotFound("worker@test.com", "test", new ArrayList<Role>(Arrays.asList(workerRole)));
        createUserIfNotFound("customer@test.com", "test", new ArrayList<Role>(Arrays.asList(customerRole)));
        createUserIfNotFound("admin@test.com", "test", new ArrayList<Role>(Arrays.asList(adminRole)));

        alreadySetup = true;

	}
	
    @Transactional
    private final Privilege createPrivilegeIfNotFound(final String name) {
        Privilege privilege = privilegeRepository.findByName(name);
        if (privilege == null) {
            privilege = new Privilege();
            privilege.setName(name);
            privilege = privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    private final Role createRoleIfNotFound(final String name, final Collection<Privilege> privileges) {
        Role role = roleRepository.findByName(name);
        if (role == null) {
            role = new Role();
            role.setName(name);
        }
        role.setPrivileges(privileges);
        role = roleRepository.save(role);
        return role;
    }

    @Transactional
    private final TidyUser createUserIfNotFound(final String email, final String password, final Collection<Role> roles) {
        TidyUser user =
        		userRepository
        		.findByEmail(email)
        		.map(u -> u)
        		.orElseGet(() ->
        		{
        			TidyUser u = new TidyUser();
                    u.setPassword(passwordEncoder.encode(password));
                    u.setEmail(email);
                    return u;
        		});
        
        user.setRoles(roles);
        user = userRepository.save(user);
        return user;
    }

}
