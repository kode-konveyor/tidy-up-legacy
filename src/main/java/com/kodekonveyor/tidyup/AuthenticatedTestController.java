package com.kodekonveyor.tidyup;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AuthenticatedTestController {
	@GetMapping("/test")
	@ResponseStatus(value = HttpStatus.OK)
	public @ResponseBody String test() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
	}
}
