package com.ij026.team3.mfpe.offersmicroservice.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ij026.team3.mfpe.offersmicroservice.dao.CommentsRepository;
import com.ij026.team3.mfpe.offersmicroservice.feign.AuthFeign;
import com.ij026.team3.mfpe.offersmicroservice.model.Comment;
import com.ij026.team3.mfpe.offersmicroservice.service.OfferService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
@CrossOrigin
public class CommentController {
	private ConcurrentHashMap<String, Object> empIdCache = new ConcurrentHashMap<>();

	public CommentController() {
		empIdCache.put("guru", new Object());
		empIdCache.put("nikky", new Object());
		empIdCache.put("subsa", new Object());
		empIdCache.put("rish", new Object());
		empIdCache.put("ujjw", new Object());
	}

	@Autowired
	private OfferService offerService;

	@Autowired
	private CommentsRepository commentsRepository;

	@Autowired
	private AuthFeign authFeign;

	private boolean isAuthorized(String jwtToken) {
		try {
			log.debug("calling authFeign client");
			ResponseEntity<String> authorizeToken = authFeign.authorizeToken(jwtToken);
			boolean ok = (authorizeToken.getStatusCodeValue() == 200);
			log.debug("authorization status {}", authorizeToken.getStatusCodeValue());
			if (ok) {
				System.err.println("Authorized");
			} else {
				System.err.println("Not Authorized");
			}
			return ok;
		} catch (Exception e) {
			System.err.println("Connection failure");
			return false;
		}
	}

	@GetMapping("/offers/{offerId}/comments")
	public ResponseEntity<List<Comment>> getComments(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId) {
		log.debug("Calling getComments");

		if (isAuthorized(jwtToken)) {
			if (offerService.checkIfPresent(offerId)) {
				return ResponseEntity.ok(commentsRepository.findByOfferId(offerId));
			} else {
				log.debug("offerId {} is invalid", offerId);
				return ResponseEntity.badRequest().build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/offers/{offerId}/comments")
	public ResponseEntity<Comment> postComment(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId, @Valid @RequestBody Comment comment) {
		log.debug("Calling postComment");

		if (isAuthorized(jwtToken)) {
			if (offerService.checkIfPresent(offerId) && empIdCache.containsKey(comment.getCommenterId())) {
				comment.setOfferId(offerId);
				comment.setDate(LocalDate.now());
				Comment save = commentsRepository.save(comment);
				log.debug("Comment created");
				return ResponseEntity.ok(save);
			} else {
				log.debug("offerId {} or commenterId {} is invalid", offerId, comment.getCommenterId());
				return ResponseEntity.badRequest().build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@DeleteMapping("/offers/{offerId}/comments/{commentId}")
	public ResponseEntity<Boolean> deleteComment(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId, @PathVariable long commentId) {
		log.debug("Calling deleteComment");

		if (isAuthorized(jwtToken)) {
			if (offerService.checkIfPresent(offerId)) {
				commentsRepository.deleteById(commentId);
				log.debug("Comment deleted");
				return ResponseEntity.ok(true);
			} else {
				log.debug("offerId {} is invalid", offerId);
				return ResponseEntity.badRequest().build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
}
