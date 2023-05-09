package com.ij026.team3.mfpe.offersmicroservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ij026.team3.mfpe.offersmicroservice.feign.AuthFeign;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;
import com.ij026.team3.mfpe.offersmicroservice.service.OfferService;

@SpringBootTest
class OfferServiceControllerTest {

	@MockBean
	private OfferService offerService;
//
//	@Bean
//	private DateTimeFormatter dateTimeFormatter() {
//		return DateTimeFormatter.ofPattern("dd-MM-yyyy");
//	}

	@MockBean
	private AuthFeign authFeign;

	@InjectMocks
	@Autowired
	private OfferServiceController offerServiceController;

	private String jwtToken_valid;
	private String jwtToken_inValid;
	private Offer offer1;
	private String empId_inValid;
	private String empId_valid;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		empId_inValid = "axxx";
		empId_valid = "subsa";
		jwtToken_valid = "valid";
		jwtToken_inValid = "in_valid";
		offer1 = new Offer();
		offer1.setOfferId(1); // Setting Primary keys for better uses
		offer1.setAuthorId(empId_valid);
	}

	@AfterEach
	void tearDown() throws Exception {
		jwtToken_inValid = jwtToken_valid = null;
		offer1 = null;
		empId_inValid = null;
		empId_valid = null;
	}

	@Test
	void testTest() {
		assertEquals("aaa", offerServiceController.test(new HashMap<>()));
	}

	@Test
	void testGetOffers() {
		when(this.offerService.allOffers()).thenReturn(List.of());
		assertEquals(List.of(), this.offerServiceController.getOffers().getBody());
	}

	@Test
	void testGetOfferCategories() {
		assertEquals(Arrays.toString(OfferCategory.values()),
				Arrays.toString(this.offerServiceController.getOfferCategories().getBody()));
	}

	@Test
	void testGetOfferDetails_allValid() {
		Offer o = new Offer();

		o.setOfferId(0);

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok("subsa"));
		when(this.offerService.getOffer(0)).thenReturn(Optional.of(o));

		assertEquals(ResponseEntity.ok(o), this.offerServiceController.getOfferDetails(jwtToken_valid, 0));
	}

	@Test
	void testGetOfferDetails_inValidOffer() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok("subsa"));
		when(this.offerService.getOffer(0)).thenReturn(Optional.empty());

		assertEquals(ResponseEntity.status(HttpStatus.NOT_FOUND).build(),
				this.offerServiceController.getOfferDetails(jwtToken_valid, 0));
	}

	@Test
	void testGetOfferDetails_inValidJwtToken() {
		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenThrow(new RuntimeException());

		when(this.offerService.getOffer(0)).thenReturn(Optional.empty());

		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
				this.offerServiceController.getOfferDetails(jwtToken_valid, 0));
	}

	@Test
	void testGetOfferDetails_ExceptionFromServiceLayer() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok("subsa"));

		when(this.offerService.getOffer(0)).thenThrow(new RuntimeException());

		assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
				this.offerServiceController.getOfferDetails(jwtToken_valid, 0));
	}

	@Test
	void testGetOfferDetailsByCategory_allValid() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok("subsa"));
		when(this.offerService.getOffersByCategory(OfferCategory.COMPUTER_ACCESORIES)).thenReturn(List.of());
		assertEquals(List.of(), this.offerServiceController
				.getOfferDetailsByCategory(jwtToken_valid, OfferCategory.COMPUTER_ACCESORIES).getBody());
	}

	@Test
	void testGetOfferDetailsByCategory_inValidJwtToken() {
//		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenThrow(new RuntimeException());
		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());
		when(this.offerService.getOffersByCategory(OfferCategory.COMPUTER_ACCESORIES)).thenReturn(List.of());
		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), this.offerServiceController
				.getOfferDetailsByCategory(jwtToken_inValid, OfferCategory.COMPUTER_ACCESORIES));
	}

	@Test
	void testGetOfferDetailsByCategory_AuthFeignClientException() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenThrow(new RuntimeException());
		when(this.offerService.getOffersByCategory(OfferCategory.COMPUTER_ACCESORIES)).thenReturn(List.of());
		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(), this.offerServiceController
				.getOfferDetailsByCategory(jwtToken_valid, OfferCategory.COMPUTER_ACCESORIES));
	}

	@Test
	void testGetOfferDetailsByCategory_ExceptionFromDAO() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok("subsa"));
		when(this.offerService.getOffersByCategory(OfferCategory.COMPUTER_ACCESORIES))
				.thenThrow(new RuntimeException());
		assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(), this.offerServiceController
				.getOfferDetailsByCategory(jwtToken_valid, OfferCategory.COMPUTER_ACCESORIES));
	}

	@Test
	void testGetOfferDetailsByLikes_allValid() {

		final Predicate<Offer> empIdFilter = o -> o.getAuthorId().equals(this.empId_valid);
		final int limit = 3;
		System.out.println(empIdFilter.hashCode());

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		when(this.offerService.getTopNOffers(limit, empIdFilter)).thenReturn(List.of(offer1));

		assertEquals(List.of(),
				this.offerServiceController.getOfferDetailsByLikes(jwtToken_valid, limit, empId_valid).getBody());
	}

	@Test
	void testGetOfferDetailsByLikes_InvalidJwt() {
		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());

		Integer limit = 3;

		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
				this.offerServiceController.getOfferDetailsByLikes(jwtToken_valid, limit, empId_valid));
	}

	@Test
	void testGetOfferDetailsByLikes_InvalidEmpId() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		Integer limit = 3;

		assertEquals(ResponseEntity.status(HttpStatus.BAD_REQUEST).build(),
				this.offerServiceController.getOfferDetailsByLikes(jwtToken_valid, limit, empId_inValid));
	}

	@Test
	void testGetOfferDetailsByPostDate_allValid() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		ResponseEntity<List<Object>> expected = ResponseEntity.ok(List.of());
		ResponseEntity<List<Offer>> actual = this.offerServiceController.getOfferDetailsByPostDate(jwtToken_valid,
				"22-05-2021");

		assertEquals(expected, actual);
	}

	@Test
	void testGetOfferDetailsByPostDate_invalidJwt() {
		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());

		ResponseEntity<List<Object>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		ResponseEntity<List<Offer>> actual = this.offerServiceController.getOfferDetailsByPostDate(jwtToken_inValid,
				"22-05-2021");

		assertEquals(expected, actual);
	}

	@Test
	void testGetOfferDetailsByPostDate_invalidDateString_createdAt() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		ResponseEntity<List<Object>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		ResponseEntity<List<Offer>> actual = this.offerServiceController.getOfferDetailsByPostDate(jwtToken_valid,
				"-05-2021");

		assertEquals(expected, actual);
	}

	@Test
	void testGetOfferDetailsByPostDate_ExceptionFrom_serviceLayer() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		LocalDate createdAt = LocalDate.of(2021, 02, 02);

		when(this.offerService.getOffersByCreationDate(createdAt)).thenThrow(new RuntimeException());

		ResponseEntity<List<Object>> expected = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

		ResponseEntity<List<Offer>> actual = this.offerServiceController.getOfferDetailsByPostDate(jwtToken_valid,
				"02-02-2021");

		assertEquals(expected, actual);
	}

	@Test
	void testGetOfferDetailsByAuthor() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.allOffers()).thenReturn(List.of(offer1));

		assertEquals(ResponseEntity.ok(List.of(offer1)),
				this.offerServiceController.getOfferDetailsByAuthor(jwtToken_valid, empId_valid));
	}

	@Test
	void testGetOfferDetailsByAuthor_RuntimeException() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.allOffers()).thenThrow(new RuntimeException());

		assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
				this.offerServiceController.getOfferDetailsByAuthor(jwtToken_valid, empId_valid));
	}

	@Test
	void testGetOfferDetailsByAuthor_invalidToken() {
		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());

		assertEquals(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(),
				this.offerServiceController.getOfferDetailsByAuthor(jwtToken_valid, empId_valid));
	}

	@Test
	void testGetOfferDetailsByAuthor_invalidEmpId() {
		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		assertEquals(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(),
				this.offerServiceController.getOfferDetailsByAuthor(jwtToken_valid, empId_inValid));
	}

	@Test
	void testAddOffer_allVaild() {
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.createOffer(offer1)).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.CREATED).body(true);
		ResponseEntity<Boolean> actual = this.offerServiceController.addOffer(jwtToken_valid, offer1);

		assertEquals(expected, actual);
	}

	@Test
	void testAddOffer_invalidJwt() {
		when(this.authFeign.authorizeToken(this.jwtToken_inValid)).thenThrow(new RuntimeException());

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<Boolean> actual = this.offerServiceController.addOffer(jwtToken_inValid, offer1);

		assertEquals(expected, actual);
	}

	@Test
	void testAddOffer_invalidAuthorId() {
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.createOffer(offer1)).thenReturn(true);

		offer1.setAuthorId(this.empId_inValid);

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		ResponseEntity<Boolean> actual = this.offerServiceController.addOffer(jwtToken_valid, offer1);

		assertEquals(expected, actual);
	}

	@Test
	void testAddOffer_whenServiceCanNotCreateOffer() {
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.createOffer(offer1)).thenReturn(false);

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(false);
		ResponseEntity<Boolean> actual = this.offerServiceController.addOffer(jwtToken_valid, offer1);

		assertEquals(expected, actual);
	}

	@Test
	void testLikeOffer_allValid() {
		Integer offerId = offer1.getOfferId();
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.getOffer(offerId)).thenReturn(Optional.of(offer1));
		when(this.offerService.updateOffer(offer1)).thenReturn(offer1);

		ResponseEntity<Offer> expected = ResponseEntity.status(HttpStatus.ACCEPTED).body(offer1);
		ResponseEntity<Offer> actual = this.offerServiceController.likeOffer(jwtToken_valid, offerId, empId_valid);

		assertEquals(expected, actual);
	}

	@Test
	void testLikeOffer_inValidEmpId() {
		Integer offerId = offer1.getOfferId();
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
//		when(this.offerService.getOffer(offerId)).thenReturn(Optional.of(offer1));
//		when(this.offerService.updateOffer(offer1)).thenReturn(offer1);

		ResponseEntity<Offer> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		ResponseEntity<Offer> actual = this.offerServiceController.likeOffer(jwtToken_valid, offerId, empId_inValid);

		assertEquals(expected, actual);
	}

	@Test
	void testLikeOffer_OfferDoesnotExist() {
		Integer offerId = offer1.getOfferId();
		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.getOffer(offerId)).thenReturn(Optional.empty());
//		when(this.offerService.updateOffer(offer1)).thenReturn(offer1);

		ResponseEntity<Offer> expected = ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		ResponseEntity<Offer> actual = this.offerServiceController.likeOffer(jwtToken_valid, offerId, empId_valid);

		assertEquals(expected, actual);
	}

	@Test
	void testLikeOffer_invalidJwt() {
		Integer offerId = offer1.getOfferId();
		when(this.authFeign.authorizeToken(this.jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());
//		when(this.offerService.getOffer(offerId)).thenReturn(Optional.of(offer1));
//		when(this.offerService.updateOffer(offer1)).thenReturn(offer1);

		ResponseEntity<Offer> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<Offer> actual = this.offerServiceController.likeOffer(jwtToken_inValid, offerId, empId_valid);

		assertEquals(expected, actual);
	}

	@Test
	void testBuyOffer_AllValid() {
		String buyerId = "ujjw";
		int offerId = offer1.getOfferId();

		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.buyOffer(buyerId, offerId)).thenReturn(true);
		when(this.offerService.ifOfferExists(offerId)).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.ok(true);
		ResponseEntity<Boolean> actual = this.offerServiceController.buyOffer(jwtToken_valid, offerId, buyerId);

		assertEquals(expected, actual);
	}

	@Test
	void testBuyOffer_invalidJwtToken() {
		String buyerId = "ujjw";
		int offerId = offer1.getOfferId();

		when(this.authFeign.authorizeToken(this.jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());
//		when(this.offerService.buyOffer(buyerId, offerId)).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<Boolean> actual = this.offerServiceController.buyOffer(jwtToken_inValid, offerId, buyerId);

		assertEquals(expected, actual);
	}

	@Test
	void testBuyOffer_invalidBuyerId() {
		String buyerId = "akajajaja";
		int offerId = offer1.getOfferId();

		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.buyOffer(buyerId, offerId)).thenReturn(true);
		when(this.offerService.ifOfferExists(offerId)).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.badRequest().body(false);
		ResponseEntity<Boolean> actual = this.offerServiceController.buyOffer(jwtToken_valid, offerId, buyerId);

		assertEquals(expected, actual);
	}

	@Test
	void testBuyOffer_offerAlreadyClosed() {

		String buyerId = "ujjw";
		int offerId = offer1.getOfferId();

		when(this.authFeign.authorizeToken(this.jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.buyOffer(buyerId, offerId)).thenReturn(false);
		when(this.offerService.ifOfferExists(offerId)).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.badRequest().body(false);
		ResponseEntity<Boolean> actual = this.offerServiceController.buyOffer(jwtToken_valid, offerId, buyerId);

		assertEquals(expected, actual);

	}

}
