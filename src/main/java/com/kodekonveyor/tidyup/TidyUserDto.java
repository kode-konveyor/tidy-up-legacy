package com.kodekonveyor.tidyup;

import java.util.Collection;

import javax.persistence.Entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TidyUserDto {
	private String email;
	private String password;
	private RoleDto role;
}
