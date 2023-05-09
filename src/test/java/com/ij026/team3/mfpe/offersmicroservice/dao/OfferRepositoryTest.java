package com.ij026.team3.mfpe.offersmicroservice.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.ij026.team3.mfpe.offersmicroservice.model.Like;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class OfferRepositoryTest {

	@Mock
	private OfferRepository offerRepository;

	private Offer offer;
	private Like like;
	private List<Like> likes;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		likes = new ArrayList<>();
		like = new Like("guru", LocalDate.now());
		likes.add(like);
		offer = new Offer(1, "subsa", LocalDate.now(), null, "", null, OfferCategory.COMPUTER_ACCESORIES, null, 0);
	}

	@AfterEach
	void tearDown() throws Exception {
		like = null;
		likes = null;
		offer = null;
	}

	@Test
	void testFindByAuthorId() {
		List<Offer> expected = new ArrayList<>();
		expected.add(offer);
		when(offerRepository.findByAuthorId("subsa")).thenReturn(expected);
		List<Offer> actual = offerRepository.findByAuthorId("subsa");
		assertEquals(actual, expected);
	}

	@Test
	void testFindByAuthorId_whenInvalidAuthorId() {
		List<Offer> expected = new ArrayList<>();
		when(offerRepository.findByAuthorId("xyz")).thenReturn(expected);
		List<Offer> actual = offerRepository.findByAuthorId("subsa");
		assertEquals(actual, expected);
	}

	@Test
	void testFindByOfferId() {
		when(offerRepository.findByOfferId(0)).thenReturn(Optional.empty());
		assertEquals(Optional.empty(), offerRepository.findByOfferId(0));
	}
}
