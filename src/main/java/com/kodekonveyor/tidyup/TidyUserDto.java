package com.kodekonveyor.tidyup;

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
