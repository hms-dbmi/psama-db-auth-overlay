package edu.harvard.hms.dbmi.avillach.auth.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;

public class PasswordUtils {

	
	public static byte[] getSalt() {
        Random rand = new Random();
        //this length must match the binary column in the DB
        byte[] salt = new byte[16];
        rand.nextBytes(salt);
        return salt;
	}
	
	public static String calculatePasswordHash(String password, byte[] salt) {
		try {
            
            // Create MessageDigest instance for MD5
            MessageDigest md= MessageDigest.getInstance("MD5");
            
            if(salt != null && salt.length > 0) {
            	md.update(salt);
            }
            
            //Add password bytes to digest
            md.update(password.getBytes());
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
            return sb.toString();
        } 
        catch (NoSuchAlgorithmException e) 
        {
            e.printStackTrace();
        }
		//shouldn't get here
		return null;
	}

}
