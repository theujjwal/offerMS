package com.ij026.team3.mfpe.offersmicroservice.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;

public interface GenericOfferService {

	boolean createOffer(Offer offer);

	boolean updateOffer(int offerId, Offer offer);

	boolean likeOffer(String authorId, String likerEmpId, int offerId);

	boolean buyOffer(String buyerId, int offerId);

	// {"no_likes" : 25, .....}
	Map<String, String> offerStatus(int offerId);

	List<Offer> getOffersByCategory(OfferCategory offerCategory);

	/**
	 * @param minLikes
	 * @return all offers with minimum {minLikes}
	 */
	List<Offer> getTopOffers();

	List<Offer> getOffersByCreationDate(LocalDate createdAt);

	List<Offer> getTopNOffers(int n, Predicate<Offer> predicate);

	/*
	 * long numberOfLikesInFirstTwoDays(int offerId);
	 * 
	 * long numberOfLikesInLastTwoDays(int offerId);
	 */
}
