package com.kodekonveyor.tidyup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class AuthenticatedTestController {
    @GetMapping("/test")
    @ResponseStatus(value = HttpStatus.OK)
    public void test(final HttpServletRequest request, final HttpServletResponse response) {
    	
    }
}
