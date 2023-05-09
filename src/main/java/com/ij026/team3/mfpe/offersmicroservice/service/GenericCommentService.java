package com.ij026.team3.mfpe.offersmicroservice.service;

import java.util.function.Predicate;

import com.ij026.team3.mfpe.offersmicroservice.model.Comment;

public interface GenericCommentService {

	boolean addComment(int offerId, Comment comment);

	boolean removeComment(long commentId);

	void updateComment(long commentId, String commentText);

	long countComment(Predicate<Comment> filter);
}
