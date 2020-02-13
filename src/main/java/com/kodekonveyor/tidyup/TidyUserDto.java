package com.kodekonveyor.tidyup;

import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.Email;

@Getter
@Setter
public class TidyUserDto {
	@Email
	private String email;
	private String password;
	private RoleDto role;
}
