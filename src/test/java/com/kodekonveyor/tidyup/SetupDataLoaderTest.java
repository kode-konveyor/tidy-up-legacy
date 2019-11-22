package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

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

	@Test
	public void call() {
		SetupDataLoader loader = new SetupDataLoader(this.userRepository,this.roleRepository,this.privilegeRepository,this.passwordEncoder);
		
		when(userRepository.findByEmail(SetupDataLoader.getWORKER_TEST_COM())).thenReturn(Optional.empty());
		when(userRepository.findByEmail(SetupDataLoader.getADMIN_TEST_COM())).thenReturn(user());
		when(userRepository.findByEmail(SetupDataLoader.getCUSTOMER_TEST_COM())).thenReturn(Optional.empty());
		when(roleRepository.findByName(SetupDataLoader.getADMIN_ROLE())).thenReturn(new UserRole());
		when(roleRepository.findByName(RoleDto.CUSTOMER_ROLE.toString())).thenReturn(null);
		when(roleRepository.findByName(RoleDto.WORKER_ROLE.toString())).thenReturn(null);
		when(privilegeRepository.findByName(SetupDataLoader.getADMIN_PRIVILEGE())).thenReturn(null);
		when(privilegeRepository.findByName(SetupDataLoader.getWORKER_PRIVILEGE())).thenReturn(new Privilege());
		when(privilegeRepository.findByName(SetupDataLoader.getCUSTOMER_PRIVILEGE())).thenReturn(null);

		when(userRepository.save(Mockito.any())).thenAnswer(new Answer<>() {
			@Override
		    public Object answer(final InvocationOnMock invocation) {
		        return invocation.getArguments()[0];
		    }
		});
		
		when(roleRepository.save(Mockito.any())).thenAnswer(new Answer<>() {
			@Override
		    public Object answer(final InvocationOnMock invocation) {
		        return invocation.getArguments()[0];
		    }
		});
		
		when(privilegeRepository.save(Mockito.any())).thenAnswer(new Answer<>() {
			@Override
		    public Object answer(final InvocationOnMock invocation) {
		        return invocation.getArguments()[0];
		    }
		});
		
		loader.onApplicationEvent(event);
		loader.onApplicationEvent(event);
		assertThat(loader.isAlreadySetup()).isEqualTo(true);
	}
	
	@Test
	public void call2() {
		SetupDataLoader loader = new SetupDataLoader(this.userRepository,this.roleRepository,this.privilegeRepository,this.passwordEncoder);
		assertThat(loader.isAlreadySetup()).isEqualTo(false);
	}

}
