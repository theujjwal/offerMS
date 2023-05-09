package com.ij026.team3.mfpe.offersmicroservice.service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ij026.team3.mfpe.offersmicroservice.dao.OfferRepository;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class OfferService implements GenericOfferService {

	@Autowired
	private OfferRepository offerRepository;

	public boolean ifOfferExists(int offerId) {
		return offerRepository.existsById(offerId);
	}

	public Collection<Offer> allOffers() {
		return offerRepository.findAll();
	}

	public Optional<Offer> getOffer(int offerId) {
		return offerRepository.findByOfferId(offerId);
	}

	public List<Offer> getOfferByAuthorId(String authorId) {
		return offerRepository.findByAuthorId(authorId);
	}

	public Offer updateOffer(Offer offer) {
		return offerRepository.save(offer);
	}

	@Override
	public boolean createOffer(Offer offer) {
//		if (checkIfPresent(offer.getOfferId()))
//			return false;
		Offer save = offerRepository.save(offer);
		System.out.println(save);
		return save != null;
	}

	// ...../offers/{id} [POST] => request Body => offer
	@Override
	public boolean updateOffer(int offerId, Offer offer) {
		if (offerId == offer.getOfferId()) {
			if (checkIfPresent(offerId)) {
				Offer save = offerRepository.save(offer);
				return save != null;
			}
		}
		return false;
	}

	public boolean checkIfPresent(int offerId) {
		return offerRepository.existsById(offerId);
	}

	@Override
	public boolean likeOffer(String authorId, String likerEmpId, int offerId) {
		Optional<Offer> foundByOfferId = offerRepository.findByOfferId(offerId);
		if (foundByOfferId.isPresent()) {
			foundByOfferId.get().like(likerEmpId);
			offerRepository.save(foundByOfferId.get());
			return true;
		}
		return false;
	}

	@Override
	public boolean buyOffer(String buyerId, int offerId) {
		Optional<Offer> foundByOfferId = offerRepository.findByOfferId(offerId);
		if (foundByOfferId.isPresent()) {
//			Version 1
//			foundByOfferId.get().setBuyerId(buyerId);
//			Offer save = offerRepository.save(foundByOfferId.get());
//			return save != null;

//			Version 2
			Offer offer = foundByOfferId.get();
			boolean buy = offer.buy(buyerId);
			if (buy) {
				return offerRepository.save(offer) != null;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public Map<String, String> offerStatus(int offerId) {
		Map<String, String> matrix = new HashMap<>();
		Optional<Offer> foundByOfferId = offerRepository.findByOfferId(offerId);
		if (foundByOfferId.isPresent()) {
			log.debug("preparing offer status for offerId {}", offerId);
			buildMatrix(matrix, foundByOfferId);
			return matrix;
		} else {
			return matrix;
		}
	}

	public void buildMatrix(Map<String, String> matrix, Optional<Offer> foundByOfferId) {
		Offer offer = foundByOfferId.get();
		matrix.put("Author ID", offer.getAuthorId());
		if (offer.getBuyerId() != null) {
			matrix.put("Buyer ID", offer.getBuyerId());
		}
		matrix.put("Category", offer.getOfferCategory().toString());
		matrix.put("Details", offer.getDetails());
		matrix.put("Open status", Boolean.toString(offer.isOpen()));
		matrix.put("Likes", offer.getLikes().toString());
	}

	@Override
	public List<Offer> getOffersByCategory(OfferCategory offerCategory) {
		return offerRepository.findByOfferCategory(offerCategory);
	}

	@Override
	public List<Offer> getTopOffers() {
		List<Offer> collect = offerRepository.findAll().stream()
				.sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size()).collect(Collectors.toList());
		return collect;
	}

	@Override
	public List<Offer> getTopNOffers(int n, Predicate<Offer> predicate) {
		if (n > 0) {
			// reverse sort on likes {max to min}
			List<Offer> collect = offerRepository.findAll().stream().filter(predicate)
					.sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size()).collect(Collectors.toList());
			return collect.subList(0, Math.min(collect.size(), n));
		} else {
			return List.of();
		}
	}

	@Override
	public List<Offer> getOffersByCreationDate(@NotNull LocalDate createdAt) {
		return offerRepository.findByCreatedAt(createdAt);
	}

}
