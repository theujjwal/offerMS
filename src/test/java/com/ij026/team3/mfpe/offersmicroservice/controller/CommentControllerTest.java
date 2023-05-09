package com.ij026.team3.mfpe.offersmicroservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ij026.team3.mfpe.offersmicroservice.dao.CommentsRepository;
import com.ij026.team3.mfpe.offersmicroservice.feign.AuthFeign;
import com.ij026.team3.mfpe.offersmicroservice.model.Comment;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.service.OfferService;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class CommentControllerTest {
	private String jwtToken_valid;
	private String jwtToken_inValid;
	private Offer offer1;
	private String empId_inValid;
	private String empId_valid;
	private Comment validComment;
	private Comment inValidComment;

	@MockBean
	private OfferService offerService;

	@MockBean
	private CommentsRepository commentsRepository;

	@MockBean
	private AuthFeign authFeign;

	@Autowired
	@InjectMocks
	private CommentController commentController;

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

		validComment = new Comment(1, empId_valid, offer1.getOfferId(), "valid", LocalDate.now());
		inValidComment = new Comment(100, empId_inValid, offer1.getOfferId(), "invalid", LocalDate.now());
	}

	@AfterEach
	void tearDown() throws Exception {
		jwtToken_inValid = jwtToken_valid = null;
		offer1 = null;
		empId_inValid = null;
		empId_valid = null;
		validComment = inValidComment = null;
	}

	@Test
	void testGetComments_allValid() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(true);
		when(this.commentsRepository.findByOfferId(offer1.getOfferId())).thenReturn(List.of(this.validComment));

		ResponseEntity<List<Comment>> expected = ResponseEntity.ok(List.of(this.validComment));
		ResponseEntity<List<Comment>> actual = this.commentController.getComments(jwtToken_valid, offer1.getOfferId());

		assertEquals(expected, actual);
	}

	@Test
	void testGetComments_whenInvalidJwtToken() {

		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());

		ResponseEntity<List<Comment>> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<List<Comment>> actual = this.commentController.getComments(jwtToken_valid, offer1.getOfferId());

		assertEquals(expected, actual);
	}

	@Test
	void testGetComments_whenOfferId_inValid() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(false);

		ResponseEntity<List<Comment>> expected = ResponseEntity.badRequest().build();
		ResponseEntity<List<Comment>> actual = this.commentController.getComments(jwtToken_valid, offer1.getOfferId());

		assertEquals(expected, actual);
	}

	@Test
	void testPostComment_allValid() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(true);
		when(this.commentsRepository.save(validComment)).thenReturn(validComment);

		ResponseEntity<Comment> expected = ResponseEntity.ok(validComment);
		ResponseEntity<Comment> actual = this.commentController.postComment(jwtToken_valid, offer1.getOfferId(),
				validComment);

		assertEquals(expected, actual);
	}

	@Test
	void testPostComment_whenOfferId_inValid() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(false);
		when(this.commentsRepository.save(validComment)).thenReturn(validComment);

		ResponseEntity<Comment> expected = ResponseEntity.badRequest().build();
		ResponseEntity<Comment> actual = this.commentController.postComment(jwtToken_valid, offer1.getOfferId(),
				validComment);

		assertEquals(expected, actual);
	}

	@Test
	void testPostComment_whenJwtToken_inValid() {

		when(this.authFeign.authorizeToken(jwtToken_inValid)).thenReturn(ResponseEntity.badRequest().build());

		ResponseEntity<Comment> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<Comment> actual = this.commentController.postComment(jwtToken_inValid, offer1.getOfferId(),
				validComment);

		assertEquals(expected, actual);
	}

	@Test
	void testPostComment_when_inValidComment() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));

		ResponseEntity<Comment> expected = ResponseEntity.badRequest().build();
		ResponseEntity<Comment> actual = this.commentController.postComment(jwtToken_valid, offer1.getOfferId(),
				inValidComment);

		assertEquals(expected, actual);
	}

	@Test
	void testDeleteComment_allValid() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(true);

		ResponseEntity<Boolean> expected = ResponseEntity.ok(true);
		ResponseEntity<Boolean> actual = this.commentController.deleteComment(jwtToken_valid,
				offer1.getOfferId().intValue(), validComment.getId());

		assertEquals(expected, actual);
	}

	@Test
	void testDeleteComment_whenNoSuchOffersFor_offerId() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.ok(this.empId_valid));
		when(this.offerService.checkIfPresent(offer1.getOfferId())).thenReturn(false);

		ResponseEntity<Boolean> expected = ResponseEntity.badRequest().build();
		ResponseEntity<Boolean> actual = this.commentController.deleteComment(jwtToken_valid,
				offer1.getOfferId().intValue(), validComment.getId());

		assertEquals(expected, actual);
	}

	@Test
	void testDeleteComment_whenInvalidJwtToken() {

		when(this.authFeign.authorizeToken(jwtToken_valid)).thenReturn(ResponseEntity.badRequest().build());

		ResponseEntity<Boolean> expected = ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		ResponseEntity<Boolean> actual = this.commentController.deleteComment(jwtToken_valid,
				offer1.getOfferId().intValue(), validComment.getId());

		assertEquals(expected, actual);
	}

}
