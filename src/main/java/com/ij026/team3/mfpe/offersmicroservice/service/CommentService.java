package com.ij026.team3.mfpe.offersmicroservice.service;

import java.time.LocalDate;
import java.util.function.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ij026.team3.mfpe.offersmicroservice.UserDetailsLoader;
import com.ij026.team3.mfpe.offersmicroservice.dao.CommentsRepository;
import com.ij026.team3.mfpe.offersmicroservice.dao.OfferRepository;
import com.ij026.team3.mfpe.offersmicroservice.model.Comment;

@Service
public class CommentService implements GenericCommentService {

	@Autowired
	private CommentsRepository commentsRepository;
	@Autowired
	private OfferRepository offerRepository;
	@Autowired
	private UserDetailsLoader loader;

	@Override
	public boolean addComment(int offerId, Comment comment) {
		boolean ifPresent = loader.ifPresent(comment.getCommenterId());
		if (ifPresent) {
			boolean existsById = offerRepository.existsById(offerId);
			System.err.println(existsById);
			if (existsById) {
				comment.setOfferId(offerId);
				comment.setDate(LocalDate.now());
				commentsRepository.save(comment);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean removeComment(long commentId) {
		if (commentsRepository.existsById(commentId)) {
			commentsRepository.deleteById(commentId);
			return true;
		}
		return false;
	}

	@Override
	public void updateComment(long commentId, String commentText) {
		commentsRepository.findById(commentId).ifPresent(c -> {
			c.setCommenterId(commentText);
			commentsRepository.save(c);
		});
	}

	@Override
	public long countComment(Predicate<Comment> filter) {
		return 0l;
	}

	/**
	 * 1. First fetch all offers 2. For each offer count comments
	 * 
	 */
	public long countComment_FirstTwoDays(String authorId) {
		return 0;
	}

}
