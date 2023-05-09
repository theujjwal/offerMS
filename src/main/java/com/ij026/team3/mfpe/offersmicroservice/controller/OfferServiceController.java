package com.ij026.team3.mfpe.offersmicroservice.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ij026.team3.mfpe.offersmicroservice.feign.AuthFeign;
import com.ij026.team3.mfpe.offersmicroservice.model.Offer;
import com.ij026.team3.mfpe.offersmicroservice.model.OfferCategory;
import com.ij026.team3.mfpe.offersmicroservice.service.OfferService;

import lombok.extern.log4j.Log4j2;

/**
 * @author Subham Santra
 *
 */
@RestController
@CrossOrigin
@Log4j2
public class OfferServiceController {

	private ConcurrentHashMap<String, Object> empIdCache = new ConcurrentHashMap<>();

	public OfferServiceController() {
		empIdCache.put("guru", new Object());
		empIdCache.put("nikky", new Object());
		empIdCache.put("subsa", new Object());
		empIdCache.put("rish", new Object());
		empIdCache.put("ujjw", new Object());
	}

	@Autowired
	private OfferService offerService;

	@Autowired
	private DateTimeFormatter dateTimeFormatter;

	@Autowired
	private AuthFeign authFeign;

//	BiPredicate<String, Offer> authorFilter_forOffer = (empId, offer) -> empId == null ? true
//			: offer.getAuthorId().equals(empId);

	@GetMapping("/test")
	public String test(@RequestParam(required = false) Map<String, Object> map) {
		map.forEach((s, o) -> System.err.println(s + " : " + o));
		return "aaa";
	}

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

	@GetMapping("/offers")
	public ResponseEntity<Collection<Offer>> getOffers() {
		log.debug("fetching all offers");
		return ResponseEntity.ok(offerService.allOffers());
	}

	@GetMapping("/offercategories")
	public ResponseEntity<OfferCategory[]> getOfferCategories() {
		log.debug("fetching all offerCategories");
		return ResponseEntity.ok(OfferCategory.values());
	}

	@GetMapping("/offers/{offerId}")
	public ResponseEntity<Offer> getOfferDetails(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId) {

		log.debug("Calling getOfferDetails");
		if (isAuthorized(jwtToken)) {
			try {
				Optional<Offer> offerStatus = offerService.getOffer(offerId);
				System.err.println(offerStatus);
				if (offerStatus.isPresent()) {
					log.debug("fetching offer details by offer id {} was succesfull", offerId);
					return ResponseEntity.ok(offerStatus.get());
				} else {
					log.debug("fetching offer details by offer id {} was unsuccesfull", offerId);
					return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
				}
			} catch (Exception e) {
				log.debug("exception @getOfferDetails : {}", e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/offers/search/by-category")
	public ResponseEntity<List<Offer>> getOfferDetailsByCategory(@RequestHeader(name = "Authorization") String jwtToken,
			@RequestParam(required = true) OfferCategory offerCategory) {

		log.debug("Calling getOfferDetailsByCategory");

		if (isAuthorized(jwtToken)) {
			try {
				List<Offer> offers = offerService.getOffersByCategory(offerCategory);
				log.debug("fetching offer details by category {} was succesfull", offerCategory.toString());
				return ResponseEntity.ok(offers);
			} catch (Exception e) {
				log.debug("exception @getOfferDetailsByCategory : {}", e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/offers/search/by-likes")
	public ResponseEntity<List<Offer>> getOfferDetailsByLikes(@RequestHeader(name = "Authorization") String jwtToken,
			@RequestParam(required = false, defaultValue = "3") Integer limit,
			@RequestParam(required = false) String empId) {
		log.debug("Calling getOfferDetailsByLikes");

		if (isAuthorized(jwtToken)) {
			if (empIdCache.containsKey(empId)) {
				try {

					Predicate<Offer> predicate;

					if (empId == null) {
						predicate = o -> true;
					} else {
						predicate = o -> o.getAuthorId().equals(empId);
					}

					List<Offer> offers = offerService.getTopNOffers(limit, predicate);

					log.debug("predicate : {}", predicate.hashCode());
					log.debug("fetching top offer details by likes was succesfull {}", offers);
					return ResponseEntity.ok(offers);
				} catch (Exception e) {
					log.debug("exception @getOfferDetailsByLikes : {}", e.getMessage());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
				}
			} else {
				log.debug("empId {} invalid", empId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/offers/search/by-creation-date")
	public ResponseEntity<List<Offer>> getOfferDetailsByPostDate(@RequestHeader(name = "Authorization") String jwtToken,
			@RequestParam(required = true) String createdOn) {
		log.debug("Calling getOfferDetailsByLikes");

		LocalDate createdAt = null;
		if (isAuthorized(jwtToken)) {
			try {
				createdAt = LocalDate.parse(createdOn, dateTimeFormatter);
			} catch (DateTimeException e) {
				log.debug("exception @getOfferDetailsByLikes : {}", e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			try {
				List<Offer> offers = offerService.getOffersByCreationDate(createdAt);
				log.debug("fetching top offer details by likes was succesfull");
				return ResponseEntity.ok(offers);
			} catch (Exception e) {
				log.debug("exception @getOfferDetailsByLikes : {}", e.getMessage());
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/offers/search/by-author")
	public ResponseEntity<List<Offer>> getOfferDetailsByAuthor(@RequestHeader(name = "Authorization") String jwtToken,
			@RequestParam(required = true) String authorId) {
		log.debug("Calling getOfferDetailsByAuthor");

		if (isAuthorized(jwtToken)) {
			if (empIdCache.containsKey(authorId)) {
				Predicate<Offer> filter1 = o -> o.getAuthorId().equals(authorId);
				try {
					Collection<Offer> allOffers = offerService.allOffers();
					List<Offer> offers = allOffers.stream().filter(filter1).collect(Collectors.toList());
					log.debug("fetching top offer details by likes was succesfull");
					return ResponseEntity.ok(offers);
				} catch (Exception e) {
					log.debug("exception @getOfferDetailsByAuthor : {}", e.getMessage());
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
				}
			} else {
				log.debug("authorId {} invalid", authorId);
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/offers")
	public ResponseEntity<Boolean> addOffer(@RequestHeader(name = "Authorization") String jwtToken,
			@Valid @RequestBody Offer newOffer) {
		log.debug("Calling addOffer");
		if (isAuthorized(jwtToken)) {
			if (empIdCache.containsKey(newOffer.getAuthorId())) {
				boolean b = offerService.createOffer(newOffer);
				if (b) {
					log.debug("New Offer created");
				}
				return b ? ResponseEntity.status(HttpStatus.CREATED).body(b)
						: ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(b);
			} else {
				log.debug("New Offer AuthorId {} is invalid", newOffer.getAuthorId());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/offers/{offerId}/likes")
	public ResponseEntity<Offer> likeOffer(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId, @RequestParam(required = true) String likedBy) {
		log.debug("Calling likeOffer");
		if (isAuthorized(jwtToken)) {
			if (empIdCache.containsKey(likedBy)) {
				Optional<Offer> optional = offerService.getOffer(offerId);
				if (optional.isPresent()) {
					Offer offer = optional.get();
					offer.like(likedBy);
					return ResponseEntity.status(HttpStatus.ACCEPTED).body(offerService.updateOffer(offer));
				} else {
					log.debug("No offer with offerId {} found", offerId);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
			} else {
				log.debug("likedBy empId {} invalid", likedBy);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@PostMapping("/offers/{offerId}/buy")
	public ResponseEntity<Boolean> buyOffer(@RequestHeader(name = "Authorization") String jwtToken,
			@PathVariable int offerId, @RequestParam(required = true) String buyerId) {
		log.debug("Calling buyOffer");

		if (isAuthorized(jwtToken)) {
			if (empIdCache.containsKey(buyerId)) {
				if (offerService.ifOfferExists(offerId)) {
					boolean buyOffer = offerService.buyOffer(buyerId, offerId);
					if (buyOffer) {
						log.debug("Offer with offerId {} is bought by buyerId {}", offerId, buyerId);
						return ResponseEntity.ok(true);
					} else {
						log.debug("Offer with offerId {} is closed", offerId);
						return ResponseEntity.badRequest().body(false);
					}
				}
			}
			log.debug("buyerId {} Or offerId {} invalid", buyerId, offerId);
			return ResponseEntity.badRequest().body(false);
		} else {
			log.debug("jwtToken invalid");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

}
