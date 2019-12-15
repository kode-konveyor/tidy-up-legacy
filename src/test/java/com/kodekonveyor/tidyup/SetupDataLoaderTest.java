package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;

class SetupDataLoaderTest extends TestBase {
	private final ContextRefreshedEvent event = Mockito.mock(ContextRefreshedEvent.class);
	private final TidyUserRepository userRepository = Mockito.mock(TidyUserRepository.class);
	private final RoleRepository roleRepository = Mockito.mock(RoleRepository.class);
	private final PrivilegeRepository privilegeRepository = Mockito.mock(PrivilegeRepository.class);
	private final PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

	private SetupDataLoader loader;
	private List<TidyUser> savedUsers;
	private List<UserRole> savedRoles;

	@BeforeEach
	public void setUp() {
	    this.savedUsers = new ArrayList<>();
	    this.savedRoles = new ArrayList<>();
		this.loader = new SetupDataLoader(this.userRepository,this.roleRepository,this.privilegeRepository,this.passwordEncoder);

		when(passwordEncoder.encode(Mockito.any())).thenAnswer(invocation -> {
			String password = invocation.getArgumentAt(0, String.class);
			return password;
		});
		when(userRepository.findByEmail(SetupDataLoader.getWORKER_TEST_COM())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(SetupDataLoader.getADMIN_TEST_COM())).thenReturn(user());
		when(userRepository.findByEmail(SetupDataLoader.getCUSTOMER_TEST_COM())).thenReturn(Optional.empty());
		when(roleRepository.findByName(SetupDataLoader.getADMIN_ROLE())).thenReturn(new UserRole());
		when(roleRepository.findByName(RoleDto.CUSTOMER_ROLE.toString())).thenReturn(null);
		when(roleRepository.findByName(RoleDto.WORKER_ROLE.toString())).thenReturn(null);
		when(privilegeRepository.findByName(SetupDataLoader.getADMIN_PRIVILEGE())).thenReturn(null);
		when(privilegeRepository.findByName(SetupDataLoader.getWORKER_PRIVILEGE())).thenReturn(new Privilege());
		when(privilegeRepository.findByName(SetupDataLoader.getCUSTOMER_PRIVILEGE())).thenReturn(null);

		when(userRepository.save(Mockito.any())).thenAnswer(
				invocation ->
				{
					TidyUser user = invocation.getArgumentAt(0, TidyUser.class);
			        this.savedUsers.add(user);
					return user;
				});

		when(roleRepository.save(Mockito.any())).thenAnswer(
				invocation ->
				{
					UserRole role = invocation.getArgumentAt(0, UserRole.class);
					this.savedRoles.add(role);
					return role;
				});

		when(privilegeRepository.save(Mockito.any())).thenAnswer(new Answer<>() {
			@Override
		    public Object answer(final InvocationOnMock invocation) {
		        Privilege privilege = invocation.getArgumentAt(0,Privilege.class);
		        if(privilege.getName().equals(SetupDataLoader.getWORKER_PRIVILEGE()))
		        	throw new IllegalArgumentException();
		        else
		        	return privilege;
		    }
		});
	}
	
	@Test
	public void initiallyNotAlreadySetup() {
		assertThat(loader.isAlreadySetup()).isEqualTo(false);
	}

	@Test
	public void afterEventItsSetup() {
		loader.onApplicationEvent(event);
		assertThat(loader.isAlreadySetup()).isEqualTo(true);
	}

	@Test
	public void afterTwoEventsItsSetup() {
		loader.onApplicationEvent(event);
		loader.onApplicationEvent(event);
		assertThat(loader.isAlreadySetup()).isEqualTo(true);
	}

	@Test
	public void passwordsSaved() {
		loader.onApplicationEvent(event);
		List<String> passwords =
				this.savedUsers
				.stream()
				.map(user -> user.getPassword())
				.filter(thing -> thing != null)
				.collect(Collectors.toList());

		assertThat(passwords.size()).isEqualTo(3);
	}

	@Test
	public void rolesSavedToUsers() {
		loader.onApplicationEvent(event);
		List<UserRole> passwords =
				this.savedUsers
						.stream()
						.flatMap(user -> user.getRoles().stream())
						.filter(thing -> thing != null)
						.collect(Collectors.toList());

		assertThat(passwords.size()).isEqualTo(3);
	}

	@Test
	public void rolesSaved() {
		loader.onApplicationEvent(event);
		List<String> roleNames =
				this.savedRoles
						.stream()
						.map(role -> role.getName())
                        .filter(thing -> thing != null)
						.collect(Collectors.toList());

		assertThat(roleNames.size()).isEqualTo(2);
	}

	@Test
	public void privilegesSaved() {
		loader.onApplicationEvent(event);
		List<String> roleNames =
				this.savedRoles
						.stream()
						.flatMap(role -> role.getPrivileges().stream())
						.filter(thing -> thing != null)
						.map(privilege -> privilege.getName())
						.filter(thing -> thing != null)
						.distinct()
						.collect(Collectors.toList());

		assertThat(roleNames.size()).isEqualTo(3);
	}

	@Test
	public void allEmailsAreSaved() {
		loader.onApplicationEvent(event);
		List<String> emails = this.savedUsers.stream().map(user -> user.getEmail()).collect(Collectors.toList());
		boolean hasWorker = emails.contains(SetupDataLoader.getWORKER_TEST_COM());
		boolean hasCustomer = emails.contains(SetupDataLoader.getCUSTOMER_TEST_COM());
		boolean hasUser = emails.contains(user().get().getEmail());
		assertThat(
				hasWorker
                && hasCustomer
				&& hasUser
				).isTrue();
	}
}
