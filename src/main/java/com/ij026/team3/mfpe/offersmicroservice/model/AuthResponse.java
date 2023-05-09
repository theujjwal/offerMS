package com.ij026.team3.mfpe.offersmicroservice.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class AuthResponse implements Serializable {
	private static final long serialVersionUID = -3262370992842849937L;
	private String token;
	private String authServer;
	private String client;
}
