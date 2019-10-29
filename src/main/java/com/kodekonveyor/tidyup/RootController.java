package com.kodekonveyor.tidyup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class RootController {
    @GetMapping("/")
    public ResponseEntity<RootResource> root() {
    	return ResponseEntity.ok(new RootResource());
    	
    }
}
