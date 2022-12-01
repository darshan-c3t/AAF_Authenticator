package com.c3t.authenticator.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtil {
	
	public static String convertToMd5HashString(String stringToHash) throws NoSuchAlgorithmException {
		String hashedString;
		 try {
	            // Create MessageDigest instance for MD5
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            //Add password bytes to digest
	            md.update(stringToHash.getBytes());
	            //Get the hash's bytes
	            byte[] bytes = md.digest();
	            //This bytes[] has bytes in decimal format;
	            //Convert it to hexadecimal format
	            StringBuilder sb = new StringBuilder();
	            for(int i=0; i< bytes.length ;i++)
	            {
	                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	            }
	            //Get complete hashed password in hex format
	            hashedString = sb.toString();
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	           throw e;
	        }
		 return hashedString;
	}

	public static String convertBcryptString(String stringToHash) throws NoSuchAlgorithmException {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		return passwordEncoder.encode(stringToHash);
	}

}
