package uk.ac.gcu.bluedroid;

import java.security.MessageDigest;

public class Util {

	public static String hash(String message) {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(message.getBytes());
			return new String(messageDigest.digest());
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
