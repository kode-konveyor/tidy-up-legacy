package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class TidyUser {

	@Id
	@GeneratedValue
	private Long identifier;

	private String email;

	@JsonIgnore
	private String password;

	@ManyToMany
	private Collection<UserRole> roles;

	@OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
	private Collection<WorkRequest> workRequests;

}
