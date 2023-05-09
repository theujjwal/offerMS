package com.ij026.team3.mfpe.offersmicroservice;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsLoader implements ApplicationRunner {

	private ConcurrentHashMap<String, String> userDetailsDB = new ConcurrentHashMap<String, String>();

	@Override
	public void run(ApplicationArguments args) throws Exception {
		userDetailsDB.put("guru", "abcd1234");
		userDetailsDB.put("rish", "abcd1234");
		userDetailsDB.put("subsa", "abcd1234");
		userDetailsDB.put("nikky", "abcd1234");
		userDetailsDB.put("ujjw", "abcd1234");
		System.err.println("User details prepopulated In OFFER MICROSERVICE!!");
	}

	public boolean ifPresent(String empId) {
		return userDetailsDB.containsKey(empId);
	}
}
