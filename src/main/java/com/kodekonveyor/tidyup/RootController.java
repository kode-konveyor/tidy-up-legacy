package com.kodekonveyor.tidyup;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
	@GetMapping("/")
	public ResponseEntity<RootResource> root() {
		return ResponseEntity.ok(new RootResource());
	}
}
