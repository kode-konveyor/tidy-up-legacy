package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Entity
@NoArgsConstructor
public class Privilege {
	@Id
	@GeneratedValue
	private Long id;
	private String name;

	@ManyToMany(mappedBy = "privileges")
	private Collection<Role> roles;

}
