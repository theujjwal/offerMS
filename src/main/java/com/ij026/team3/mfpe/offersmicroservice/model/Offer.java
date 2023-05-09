package com.ij026.team3.mfpe.offersmicroservice.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString
public class Offer {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer offerId;

	@NotNull
	private String authorId;

	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	@NotNull
	private LocalDate createdAt;

	@JsonFormat(shape = Shape.STRING, pattern = "dd-MM-yyyy")
	private LocalDate closedAt;

	@Column(length = 100)
	@NotNull
	private String details;

	@ElementCollection(fetch = FetchType.LAZY)
	@Builder.Default
	private List<Like> likes = new ArrayList<Like>();

	@NotNull
	private OfferCategory offerCategory;

	private String buyerId;

	@Min(value = 0)
	private double price;

	public void like(String empId) {
		Like newLike = new Like(empId, LocalDate.now());
		if (!likes.contains(newLike)) {
			likes.add(newLike);
		}
	}

	public boolean isOpen() {
		return closedAt == null;
	}

	public boolean buy(String buyerEmpId) {
		if (isOpen()) {
			closedAt = LocalDate.now();
			buyerId = buyerEmpId;
			return true;
		} else {
			return false;
		}
	}
}
