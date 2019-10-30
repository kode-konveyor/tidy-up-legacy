package com.kodekonveyor.tidyup;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;
import java.util.UUID;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("tidyuser behaviour")
public class TidyUserControllerTest {
	private static final Logger logger = LoggerFactory.getLogger(TidyUserControllerTest.class);

	@Autowired
	private TestRestTemplate restTemplate;

	@LocalServerPort
	Integer port;

	private class TestUserData {
		public String self;
		public TidyUserDto dto;
	};

	@Test
	public void notValidEmail() {
		TidyUserDto userDto = new TidyUserDto();
		userDto.setEmail(UUID.randomUUID().toString());
		userDto.setRole(RoleDto.CUSTOMER);
		userDto.setPassword("test1");

		ResponseEntity<Void> response = restTemplate.postForEntity("/users", userDto, Void.class);
		logger.info(response.toString());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
		
		// fixing
		userDto.setEmail("hello@"+userDto.getEmail());

		ResponseEntity<Void> good = restTemplate.postForEntity("/users", userDto, Void.class);
		logger.info(good.toString());

		assertThat(good.getStatusCode()).isEqualTo(HttpStatus.CREATED);
	}

	private TestUserData registerUser(Optional<String> email, RoleDto role) {
		TidyUserDto userDto = new TidyUserDto();
		userDto.setEmail(email.orElse(UUID.randomUUID().toString() + "@test.com"));
		userDto.setRole(role);
		userDto.setPassword("test1");
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<TidyUserDto> re = new HttpEntity<TidyUserDto>(userDto, headers);

		ResponseEntity<TidyUserResource> response = restTemplate.postForEntity("/users", re, TidyUserResource.class);
		logger.info(response.toString());

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		String self = response.getBody().getLink("self").getHref();
		TestUserData userData = new TestUserData();
		userData.self = self.replaceAll("^http://localhost:" + this.port.toString(), "");
		userData.dto = userDto;
		return userData;
	}

	private TestUserData registerCustomer() {
		return registerUser(Optional.empty(), RoleDto.CUSTOMER);
	}

	private TestUserData registerWorker() {
		return registerUser(Optional.empty(), RoleDto.WORKER);
	}

	@Test
	public void registerThenRequery() {
		TestUserData customer = registerCustomer();
		ResponseEntity<TidyUserResource> response = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.getForEntity(customer.self, TidyUserResource.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void alreadyRegistered() {
		TestUserData customer = registerCustomer();
		ResponseEntity<VndErrors> response = restTemplate.postForEntity("/users", customer.dto, VndErrors.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
	}

	@Test
	public void usersSeeEachothersProfile() {
		TestUserData customer = registerCustomer();
		TestUserData other = registerCustomer();
		ResponseEntity<TidyUserResource> response = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.getForEntity(other.self, TidyUserResource.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	private class TestWorkRequestData {
		public WorkRequestDto dto;
		public String selfUri;
	}
	
	private final static String CITY = "AOEU"; 

	private TestWorkRequestData addWorkRequest(TestUserData customer) {
		WorkRequestDto requestDto = new WorkRequestDto();
		requestDto.setCity(CITY);
		requestDto.setDescription("Want my space to be tidy.");
		ResponseEntity<WorkRequestResource> response = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.postForEntity(customer.self + "/workrequests", requestDto, WorkRequestResource.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		String self = response.getBody().getLink("self").getHref();
		TestWorkRequestData requestData = new TestWorkRequestData();
		requestData.selfUri = self.replaceAll("^http://localhost:" + this.port.toString(), "");
		requestData.dto = requestDto;
		return requestData;
	}

	@Test
	public void userCanAddWorkRequest() {
		TestUserData customer = registerCustomer();
		addWorkRequest(customer);
	}

	@Test
	public void userCanRequeryWorkRequest() {
		TestUserData customer = registerCustomer();
		TestWorkRequestData workRequest = addWorkRequest(customer);
		ResponseEntity<WorkRequestResource> response = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.getForEntity(workRequest.selfUri, WorkRequestResource.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void userCanDeleteWorkRequest() {
		TestUserData customer = registerCustomer();
		TestWorkRequestData workrequest = addWorkRequest(customer);
		ResponseEntity<Void> result = restTemplate.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.exchange("http://localhost:" + port.toString() + workrequest.selfUri, HttpMethod.DELETE, null, Void.class);
		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
		ResponseEntity<VndErrors> gone = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.getForEntity(workrequest.selfUri, VndErrors.class);
		assertThat(gone.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void userCanDeleteAccount() {
		TestUserData customer = registerCustomer();
		TestUserData other = registerCustomer();
		ResponseEntity<Void> response = restTemplate.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.exchange("http://localhost:" + port.toString() + customer.self, HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<VndErrors> gone = restTemplate
				.withBasicAuth(other.dto.getEmail(), other.dto.getPassword())
				.getForEntity(customer.self, VndErrors.class);
		assertThat(gone.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	public void usersCanSeeEachothersWorkrequests() {
		TestUserData customer = registerCustomer();
		TestUserData other = registerCustomer();
		TestWorkRequestData workRequest = addWorkRequest(customer);
		ResponseEntity<WorkRequestResource> response = restTemplate
				.withBasicAuth(other.dto.getEmail(), other.dto.getPassword())
				.getForEntity(workRequest.selfUri, WorkRequestResource.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		ResponseEntity<WorkRequestResource> crossRefResponse = restTemplate
				.withBasicAuth(other.dto.getEmail(), other.dto.getPassword())
				.getForEntity(customer.self + "/workrequests", WorkRequestResource.class);
		assertThat(crossRefResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	public void workersCouldNotAddWorkrequests() {
		TestUserData worker = registerWorker();
		WorkRequestDto request = new WorkRequestDto();
		request.setCity(CITY);
		request.setDescription("Want my space to be tidy.");
		ResponseEntity<WorkRequestResource> response = restTemplate
				.withBasicAuth(worker.dto.getEmail(), worker.dto.getPassword())
				.postForEntity(worker.self + "/workrequests", request, WorkRequestResource.class);
		logger.info(response.toString());
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);

	}

	@Test
	public void workersCanSearchByCity() {
		TestUserData worker = registerWorker();
		TestUserData customer = registerCustomer();
		WorkRequestDto req = new WorkRequestDto();
		req.setCity(CITY);
		req.setDescription("Want my space to be tidy.");
		ResponseEntity<WorkRequestResource> res = restTemplate
				.withBasicAuth(customer.dto.getEmail(), customer.dto.getPassword())
				.postForEntity(customer.self + "/workrequests", req, WorkRequestResource.class);
		assertThat(res.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		ResponseEntity<WorkRequestResource> bycity = restTemplate
				.withBasicAuth(worker.dto.getEmail(), worker.dto.getPassword())
				.getForEntity("/workrequests/"+CITY, WorkRequestResource.class);
		assertThat(bycity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
