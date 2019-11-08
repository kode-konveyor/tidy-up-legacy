package com.kodekonveyor.tidyup;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service("userDetailsService")
@Transactional
public class TidyUserDetailsService implements UserDetailsService {

	@Autowired
	private TidyUserRepository userRepository;
	// API

	@Override
	public UserDetails loadUserByUsername(final String email) {
		final TidyUser user = userRepository.findByEmail(email).map(u -> u)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username: " + email));

		return new User(user.getEmail(), user.getPassword(), true, true, true, true, getAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> getAuthorities(final Collection<UserRole> roles) {
		return getGrantedAuthorities(getPrivileges(roles));
	}

	private List<String> getPrivileges(final Collection<UserRole> roles) {
		return roles.stream()
                .flatMap(role -> role.getPrivileges().stream())
                .map(Privilege::getName)
                .collect(Collectors.toList());
	}

	private List<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
		return privileges.stream()
				.map(privilege -> new SimpleGrantedAuthority(privilege))
				.collect(Collectors.toList());
	}
}
