package com.ij026.team3.mfpe.offersmicroservice.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Integer> {
	List<Offer> findByAuthorId(String authorId);

	Optional<Offer> findByOfferId(int offerId);

	List<Offer> findByOfferCategory(OfferCategory offerCategory);

	List<Offer> findByCreatedAt(LocalDate createdAt);

}
