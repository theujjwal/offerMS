package com.ij026.team3.mfpe.offersmicroservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ij026.team3.mfpe.offersmicroservice.dao.OfferRepository;
import com.ij026.team3.mfpe.offersmicroservice.model.Like;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class OfferServiceTest {

	@MockBean
	private OfferRepository offerRepository;

	@Autowired
	@InjectMocks
	private OfferService offerService;

	private Offer offer;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		offer = new Offer(1, "subsa", LocalDate.now(), null, "", new ArrayList<Like>(),
				OfferCategory.COMPUTER_ACCESORIES, null, 0);
	}

	@AfterEach
	void tearDown() throws Exception {
		offer = null;
	}

	@Test
	void testIfOfferExists() {
		when(this.offerRepository.existsById(0)).thenReturn(true);
		boolean actual = offerService.ifOfferExists(0);
		assertEquals(true, actual);
	}

	@Test
	void testAllOffers() {
		when(this.offerRepository.findAll()).thenReturn(List.of(offer));
		Collection<Offer> allOffers = offerService.allOffers();
		assertEquals(List.of(offer), allOffers);
	}

	@Test
	void testGetOffer() {
		Optional<Offer> optional = Optional.of(offer);
		when(this.offerRepository.findByOfferId(0)).thenReturn(optional);
		Optional<Offer> optional2 = offerService.getOffer(0);
		assertEquals(optional, optional2);
	}

	@Test
	void testGetOfferByAuthorId() {
		List<Offer> expected = new ArrayList<>();
		expected.add(offer);

		when(this.offerRepository.findByAuthorId("subsa")).thenReturn(expected);
		List<Offer> actual = offerService.getOfferByAuthorId("subsa");
		assertEquals(expected, actual);
	}

	@Test
	void testUpdateOfferOffer() {
		when(this.offerRepository.save(offer)).thenReturn(offer);
		assertEquals(offer, this.offerService.updateOffer(offer));
	}

	@Test
	void testCreateOffer() {
		when(this.offerRepository.save(offer)).thenReturn(offer);
		assertEquals(true, this.offerService.createOffer(offer));
	}

	@Test
	void testUpdateOfferIntOffer() {
		when(this.offerRepository.existsById(1)).thenReturn(true);
		when(this.offerRepository.save(offer)).thenReturn(offer);
		when(this.offerRepository.save(offer)).thenReturn(offer);
		assertEquals(true, this.offerService.updateOffer(1, offer));
	}

	@Test
	void testCheckIfPresent() {
		when(this.offerRepository.existsById(1)).thenReturn(true);
		assertEquals(true, this.offerService.checkIfPresent(1));
	}

	@Test
	void testLikeOffer_offerPresent() {
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.of(offer));
		assertEquals(true, this.offerService.likeOffer("subsa", "guru", 1));
	}

	@Test
	void testLikeOffer_offerAbsent() {
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.empty());
		assertEquals(false, this.offerService.likeOffer("subsa", "guru", 1));
	}

	@Test
	void testBuyOffer_offerPresent_open() {
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.of(offer));
		when(this.offerRepository.save(offer)).thenReturn(offer);
		assertEquals(true, this.offerService.buyOffer("guru", 1));
	}

	@Test
	void testBuyOffer_offerPrsesnt_closed() {
		offer.setClosedAt(LocalDate.now().plusDays(5));
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.of(offer));
		when(this.offerRepository.save(offer)).thenReturn(offer);
		assertEquals(false, this.offerService.buyOffer("guru", 1));
	}

	@Test
	void testBuyOffer_offerAbsent() {
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.empty());
		assertEquals(false, this.offerService.buyOffer("guru", 1));
	}

	@Test
	void testOfferStatus_offerPresent() {
		Map<String, String> matrix = new HashMap<>();
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.of(offer));

		this.offerService.buildMatrix(matrix, Optional.of(offer));

		assertEquals(matrix, this.offerService.offerStatus(1));
	}

	@Test
	void testOfferStatus_offerAbsent() {
		Map<String, String> matrix = new HashMap<>();
		when(this.offerRepository.findByOfferId(1)).thenReturn(Optional.empty());
		assertEquals(matrix, this.offerService.offerStatus(1));
	}

	@Test
	void testGetOffersByCategoryOfferCategory() {
		List<Offer> list = new ArrayList<>();
		list.add(offer);
		when(offerRepository.findByOfferCategory(OfferCategory.COMPUTER_ACCESORIES)).thenReturn(list);
		assertEquals(list, this.offerService.getOffersByCategory(OfferCategory.COMPUTER_ACCESORIES));
	}

	@Test
	void testGetTopOffers() {
		List<Offer> list = new ArrayList<>();
		when(offerRepository.findAll()).thenReturn(list);
		assertEquals(list, this.offerService.getTopOffers());
	}

	@Test
	void testGetTopNOffers() {
		List<Offer> list = new ArrayList<>();
		when(offerRepository.findAll()).thenReturn(list);
		assertEquals(list, offerService.getTopNOffers(1, o -> o.getOfferId() == 1));
	}

	@Test
	void testGetTopNOffers_False() {
		List<Offer> list = new ArrayList<>();
		when(offerRepository.findAll()).thenReturn(list);
		assertEquals(list, offerService.getTopNOffers(0, o -> o.getOfferId() == 1));
	}

	@Test
	void testGetOffersByCreationDate() {
		List<Offer> list = new ArrayList<>();
		when(offerRepository.findByCreatedAt(LocalDate.now())).thenReturn(list);
		assertEquals(list, offerService.getOffersByCreationDate(LocalDate.now()));
	}

}
