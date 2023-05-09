package com.ij026.team3.mfpe.offersmicroservice.feign;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.ResponseEntity;

import com.ij026.team3.mfpe.offersmicroservice.model.AuthRequest;
import com.ij026.team3.mfpe.offersmicroservice.model.AuthResponse;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class AuthFeignTest {

	@Mock
	private AuthFeign authFeign;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testCreateAuthenticationToken_inValid() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest("subsa", "abcd", "ROLE_USER");
		when(authFeign.createAuthenticationToken(authenticationRequest))
				.thenReturn(ResponseEntity.badRequest().build());

		assertEquals(ResponseEntity.badRequest().build(),
				this.authFeign.createAuthenticationToken(authenticationRequest));
	}

	@Test
	void testCreateAuthenticationToken_valid() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest("subsa", "abcd", "ROLE_USER");
		AuthResponse authResponse = new AuthResponse("", "", "");

		when(authFeign.createAuthenticationToken(authenticationRequest)).thenReturn(ResponseEntity.ok(authResponse));

		assertEquals(ResponseEntity.ok(authResponse), this.authFeign.createAuthenticationToken(authenticationRequest));
	}

	@Test
	void testAuthorizeToken() {
		when(authFeign.authorizeToken("valid_token")).thenReturn(ResponseEntity.ok("subsa"));
		assertEquals(ResponseEntity.ok("subsa"), authFeign.authorizeToken("valid_token"));
	}

}
