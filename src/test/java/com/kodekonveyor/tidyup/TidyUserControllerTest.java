package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.Tuple;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import lombok.Setter;

// @RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("tidyuser behaviour")
public class TidyUserControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(TidyUserControllerTest.class);
	
    @Autowired
    private TestRestTemplate restTemplate;
    
    @LocalServerPort
    Integer port;
    
    private class U {
    	public String self;
    	public HttpEntity<TidyUserDto> u;
    };
    
    @Test
    public void notValidEmail() {
        TidyUserDto u1 = new TidyUserDto();
        u1.setEmail(UUID.randomUUID().toString());
        u1.setRole(RoleDto.CUSTOMER);
        u1.setPassword("test1");
        
        ResponseEntity<Void> r1 = restTemplate.postForEntity("/users", u1, Void.class);
        logger.info(r1.toString());
        
        assertThat(r1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
    
    private U registerUser(Optional<String> email, RoleDto role) {
        TidyUserDto u1 = new TidyUserDto();
        u1.setEmail(email.orElse(UUID.randomUUID().toString()+"@test.com"));
        u1.setRole(role);
        u1.setPassword("test1");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<TidyUserDto> re = new HttpEntity<TidyUserDto>(u1,headers);
        
        ResponseEntity<TidyUserResource> r1 = restTemplate.postForEntity("/users", re, TidyUserResource.class);
        logger.info(r1.toString());
        
        assertThat(r1.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String self = r1.getBody().getLink("self").getHref();
        U r = new U();
        r.self = self.replaceAll("^http://localhost:"+this.port.toString(), "");
        r.u = re;
        return r;
    }
    
    private U registerCustomer() {
    	return registerUser(Optional.empty(), RoleDto.CUSTOMER);
    }
    
    private U registerWorker() {
    	return registerUser(Optional.empty(), RoleDto.WORKER);
    }
    
	    @Test
	    public void registerThenRequery() {
	    	U u = registerCustomer();
	    	ResponseEntity<TidyUserResource> r = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).getForEntity(u.self,TidyUserResource.class);
	        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
	    }
	    
	    @Test
	    public void alreadyRegistered() {
	    	U u = registerCustomer();
	    	ResponseEntity<VndErrors> r = restTemplate.postForEntity("/users", u.u, VndErrors.class);
	    	assertThat(r.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	    }
	    
	    @Test
	    public void usersSeeEachothersProfile() {
	    	U u1 = registerCustomer();
	    	U u2 = registerCustomer();
	    	ResponseEntity<TidyUserResource> r = restTemplate.withBasicAuth(u1.u.getBody().getEmail(), u1.u.getBody().getPassword()).getForEntity(u2.self,TidyUserResource.class);
	        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
	    }
	    
	    private class W {
	    	public WorkRequestDto wrequest;
	    	public String self;
	    }
	    
	    private W addWorkRequest(U u) {
	    	WorkRequestDto req = new WorkRequestDto();
	    	req.setCity("AOEU");
	    	req.setDescription("Want my space to be tidy.");
	    	ResponseEntity<WorkRequestResource> res = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).postForEntity(u.self+"/workrequests", req, WorkRequestResource.class);
	    	
	    	assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	        String self = res.getBody().getLink("self").getHref();
	        W r = new W();
	        r.self = self.replaceAll("^http://localhost:"+this.port.toString(), "");
	        r.wrequest = req;
	        return r;
	    }
	    
	    @Test
	    public void userCanAddWorkRequest() {
	    	U u = registerCustomer();
	    	addWorkRequest(u);
	    }

	    @Test
	    public void userCanRequeryWorkRequest() {
	    	U u = registerCustomer();
	    	W w = addWorkRequest(u);
	    	ResponseEntity<WorkRequestResource> r = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).getForEntity(w.self,WorkRequestResource.class);
	        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);
	    }
	    
	    @Test
	    public void userCanDeleteWorkRequest() {
	    	U u = registerCustomer();
	    	W w = addWorkRequest(u);
	    	ResponseEntity<Void> r = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).exchange("http://localhost:"+port.toString()+w.self, HttpMethod.DELETE, null, Void.class);
	        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	        ResponseEntity<VndErrors> gone = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).getForEntity(w.self,VndErrors.class);
	        assertThat(gone.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    }
	    
	    @Test
	    public void userCanDeleteAccount() {
	    	U u = registerCustomer();
	    	U other = registerCustomer();
	    	ResponseEntity<Void> r = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).exchange("http://localhost:"+port.toString()+u.self, HttpMethod.DELETE, null, Void.class);
	    	assertThat(r.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	    	
	    	ResponseEntity<VndErrors> gone = restTemplate.withBasicAuth(other.u.getBody().getEmail(), other.u.getBody().getPassword()).getForEntity(u.self,VndErrors.class);
	        assertThat(gone.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	    }
	    
	    @Test
	    public void usersCanSeeEachothersWorkrequests() {
	    	U u = registerCustomer();
	    	U other = registerCustomer();
	    	W w = addWorkRequest(u);
	    	ResponseEntity<WorkRequestResource> r = restTemplate.withBasicAuth(other.u.getBody().getEmail(), other.u.getBody().getPassword()).getForEntity(w.self,WorkRequestResource.class);
	        assertThat(r.getStatusCode()).isEqualTo(HttpStatus.OK);

	        ResponseEntity<WorkRequestResource> r2 = restTemplate.withBasicAuth(other.u.getBody().getEmail(), other.u.getBody().getPassword()).getForEntity(u.self+"/workrequests",WorkRequestResource.class);
	        assertThat(r2.getStatusCode()).isEqualTo(HttpStatus.OK);
	    }
	    
	    @Test
	    public void workersCouldNotAddWorkrequests() {
	    	U u = registerWorker();
	    	WorkRequestDto req = new WorkRequestDto();
	    	req.setCity("AOEU");
	    	req.setDescription("Want my space to be tidy.");
	    	ResponseEntity<WorkRequestResource> res = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).postForEntity(u.self+"/workrequests", req, WorkRequestResource.class);
	    	logger.info(res.toString());
	    	assertThat(res.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

	    }

	    @Test
	    public void workersCanSearchByCity() {
	    	U u = registerWorker();
	    	U customer = registerCustomer();
	    	WorkRequestDto req = new WorkRequestDto();
	    	req.setCity("AOEU");
	    	req.setDescription("Want my space to be tidy.");
	    	ResponseEntity<WorkRequestResource> res = restTemplate.withBasicAuth(customer.u.getBody().getEmail(), customer.u.getBody().getPassword()).postForEntity(customer.self+"/workrequests", req, WorkRequestResource.class);
	    	assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

	    	ResponseEntity<WorkRequestResource> bycity = restTemplate.withBasicAuth(u.u.getBody().getEmail(), u.u.getBody().getPassword()).getForEntity("/workrequests/AOEU", WorkRequestResource.class);
	    	assertThat(bycity.getStatusCode()).isEqualTo(HttpStatus.OK);
	    }
	    
}
