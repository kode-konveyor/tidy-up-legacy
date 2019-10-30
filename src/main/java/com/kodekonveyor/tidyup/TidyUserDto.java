package com.kodekonveyor.tidyup;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
public class TidyUserDto {
	@Email
	private String email;
	private String password;
	private RoleDto role;
}
