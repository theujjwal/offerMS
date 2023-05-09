package com.ij026.team3.mfpe.offersmicroservice.service;

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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.ij026.team3.mfpe.offersmicroservice.UserDetailsLoader;
import com.ij026.team3.mfpe.offersmicroservice.dao.CommentsRepository;
import com.ij026.team3.mfpe.offersmicroservice.dao.OfferRepository;
import com.ij026.team3.mfpe.offersmicroservice.model.Comment;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class CommentServiceTest {

	@MockBean
	private CommentsRepository commentsRepository;
	@MockBean
	private OfferRepository offerRepository;
	@MockBean
	private UserDetailsLoader loader;

	@Autowired
	@InjectMocks
	private CommentService commentService;

	private Comment comment1;
	private Comment comment2;
	private Comment comment3;

	private List<Comment> comments;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception {
	}

	@BeforeEach
	void setUp() throws Exception {
		comments = new ArrayList<>();

		comment1 = new Comment(1, "subsa", 1, "text 1", LocalDate.now());
		comment2 = new Comment(2, "guru", 1, "text 2", LocalDate.now());
		comment3 = new Comment(3, "nikky", 1, "text 3", LocalDate.now());
	}

	@AfterEach
	void tearDown() throws Exception {
		comments = null;

		comment1 = null;
		comment2 = null;
		comment3 = null;
	}

	@Test
	void testAddComment_whenAllValid() {
		comment1.setOfferId(1);

		when(this.loader.ifPresent(comment1.getCommenterId())).thenReturn(true);
		when(this.offerRepository.existsById(1)).thenReturn(true);
		when(this.commentsRepository.save(comment1)).thenReturn(comment1);
		assertEquals(true, this.commentService.addComment(1, comment1));
	}

	@Test
	void testAddComment_whenInvalidCommenter() {
		comment1.setOfferId(1);

		when(this.loader.ifPresent(comment1.getCommenterId())).thenReturn(false);
		when(this.offerRepository.existsById(1)).thenReturn(true);
		when(this.commentsRepository.save(comment1)).thenReturn(comment1);
		assertEquals(false, this.commentService.addComment(1, comment1));
	}

	@Test
	void testAddComment_whenInvalidOffer() {
		comment1.setOfferId(1);

		when(this.loader.ifPresent(comment1.getCommenterId())).thenReturn(true);
		when(this.offerRepository.existsById(1)).thenReturn(false);
		when(this.commentsRepository.save(comment1)).thenReturn(comment1);
		assertEquals(false, this.commentService.addComment(1, comment1));
	}

	@Test
	void testRemoveComment_1() {
		when(this.offerRepository.existsById(1)).thenReturn(false);
		assertEquals(false, this.commentService.removeComment(1));
	}

	@Test
	void testRemoveComment_2() {
		when(commentsRepository.existsById(1l)).thenReturn(true);
		assertEquals(true, this.commentService.removeComment(1));
	}

	@Test
	void testUpdateComment() {
		when(commentsRepository.findById(1l)).thenReturn(Optional.of(comment1));
		when(commentsRepository.save(comment1)).thenReturn(comment1);

		this.commentService.updateComment(1l, "new text");
	}

	@Test
	void testCountComment() {
		assertEquals(0, this.commentService.countComment(p -> true));
	}

	@Test
	void testCountComment_FirstTwoDays() {
		assertEquals(0, this.commentService.countComment_FirstTwoDays("subsa"));
	}

}
