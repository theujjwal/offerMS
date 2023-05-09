package com.ij026.team3.mfpe.offersmicroservice.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ij026.team3.mfpe.offersmicroservice.model.Comment;

@Repository
public interface CommentsRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByOfferId(int offerId);

	List<Comment> findByCommenterId(String commenterId);
}
