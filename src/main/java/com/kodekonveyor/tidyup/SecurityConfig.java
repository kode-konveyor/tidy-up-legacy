package com.kodekonveyor.tidyup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	final private UserDetailsService userDetailsService;

	public SecurityConfig(final UserDetailsService userDetailsService) {
		super();
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authProvider());
	}

	@Bean
	protected DaoAuthenticationProvider authProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(encoder());
		return authProvider;
	}

	@Bean
	protected PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}

	@Override
	public void configure(final HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/").permitAll().regexMatchers(HttpMethod.POST, "/users")
				.permitAll().regexMatchers(HttpMethod.GET, "/users").hasAuthority(SetupDataLoader.getADMIN_PRIVILEGE())
				.regexMatchers(HttpMethod.GET, "/users/[0-9]*/workrequests").permitAll()
				.regexMatchers(HttpMethod.POST, "/users/[0-9]*/workrequests").hasAuthority(SetupDataLoader.getCUSTOMER_PRIVILEGE())
				.regexMatchers(HttpMethod.PUT, "/users/[0-9]*/workrequests").hasAuthority(SetupDataLoader.getCUSTOMER_PRIVILEGE())
				.regexMatchers(HttpMethod.DELETE, "/users/[0-9]*/workrequests/[0-9]*")
				.hasAuthority(SetupDataLoader.getCUSTOMER_PRIVILEGE()).anyRequest().authenticated().and().httpBasic();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}
}
