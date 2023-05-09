package com.ij026.team3.mfpe.offersmicroservice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import com.ij026.team3.mfpe.offersmicroservice.controller.CommentController;
import com.ij026.team3.mfpe.offersmicroservice.controller.OfferServiceController;
import com.ij026.team3.mfpe.offersmicroservice.dao.CommentsRepository;
import com.ij026.team3.mfpe.offersmicroservice.dao.OfferRepository;
import com.ij026.team3.mfpe.offersmicroservice.exceptionhandling.GlobalExceptionHandling;
import com.ij026.team3.mfpe.offersmicroservice.feign.AuthFeign;
import com.ij026.team3.mfpe.offersmicroservice.service.CommentService;
import com.ij026.team3.mfpe.offersmicroservice.service.OfferService;

@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
class OffersMicroserviceApplicationTests {

	@Autowired
	private UserDetailsLoader detailsLoader;

	@Autowired
	private CommentController commentController;

	@Autowired
	private OfferServiceController offerServiceController;

	@Autowired
	private OfferService offerService;

	@Autowired
	private CommentsRepository commentsRepository;

	@Autowired
	private CommentService commentService;

	@Autowired
	private OfferRepository offerRepository;

	@Autowired
	private GlobalExceptionHandling exceptionHandling;

	@Autowired
	private AuthFeign authFeign;

	@Test
	void contextLoads() {

		assertThat(detailsLoader).isNotNull();
		assertThat(commentController).isNotNull();
		assertThat(offerServiceController).isNotNull();
		assertThat(offerService).isNotNull();
		assertThat(commentService).isNotNull();
		assertThat(commentsRepository).isNotNull();
		assertThat(offerRepository).isNotNull();
		assertThat(exceptionHandling).isNotNull();
		assertThat(authFeign).isNotNull();

	}

}
