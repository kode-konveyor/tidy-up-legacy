package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.JoinColumn;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
public class Role {
		@Id @GeneratedValue
		private Long id;
		private String name;

	    @ManyToMany(mappedBy = "roles")
	    private Collection<TidyUser> users;

	    @ManyToMany
	    //@JoinTable(name = "roles_privileges", joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id"))
	    private Collection<Privilege> privileges;

}
