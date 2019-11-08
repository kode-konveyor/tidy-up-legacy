package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class Role {
	@Id
	@GeneratedValue
	private Long identifier;
	private String name;

	@ManyToMany(mappedBy = "roles")
	private Collection<TidyUser> users;

	@ManyToMany
	private Collection<Privilege> privileges;

}
