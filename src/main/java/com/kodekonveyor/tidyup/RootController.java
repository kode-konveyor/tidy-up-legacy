package com.kodekonveyor.tidyup;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class RootController {
    @GetMapping("/")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void root(final HttpServletRequest request, final HttpServletResponse response) {
    	
    }
}
