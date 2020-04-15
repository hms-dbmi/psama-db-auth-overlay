package edu.harvard.hms.dbmi.avillach.auth.service.auth;

import java.util.*;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.harvard.dbmi.avillach.util.exception.ProtocolException;
import edu.harvard.dbmi.avillach.util.response.PICSUREResponse;
import edu.harvard.hms.dbmi.avillach.auth.data.entity.Credential;
import edu.harvard.hms.dbmi.avillach.auth.data.entity.User;
import edu.harvard.hms.dbmi.avillach.auth.data.repository.RoleRepository;
import edu.harvard.hms.dbmi.avillach.auth.data.repository.UserRepository;
import edu.harvard.hms.dbmi.avillach.auth.rest.UserService;
import edu.harvard.hms.dbmi.avillach.auth.security.PasswordUtils;
import edu.harvard.hms.dbmi.avillach.auth.service.BaseEntityService;
import edu.harvard.hms.dbmi.avillach.auth.service.MailService;
import edu.harvard.hms.dbmi.avillach.auth.service.TOSService;
import edu.harvard.hms.dbmi.avillach.auth.utils.AuthUtils;

/**
 * This class provides authentication functionality. This implements an
 * authenticationService interface in the future to support different modes of
 * authentication.
 *
 * <h3>Thoughts of design</h3> The main purpose of this class is returns a token
 * that includes information of the roles of users.
 */
public class AuthenticationService  {
	private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

	@Context
	SecurityContext securityContext;
	
	@Inject
	UserRepository userRepo;

	@Inject
	RoleRepository roleRepo;

	@Inject
	TOSService tosService;

	@Inject
	MailService mailService;

	@Inject
	UserService userService;

	@Inject
	AuthUtils authUtil;
	
	public Response getToken(Map<String, String> authRequest) {

		// look up user and hashed pw from DB & compare.

		String username = authRequest.get("username");
		String password = authRequest.get("password");

		if (username == null || password == null || username.isEmpty() || password.isEmpty())
			throw new ProtocolException("Missing Username or password in request body.");

		// Do we have this user already?
		User user = userRepo.findBySubject(username);
		if (user == null) {
			throw new NotAuthorizedException("No user matching user_id " + username + " present in database");
		}

		Credential credential = user.getCredential();
		logger.info("Stored cred" + credential);
		if (credential != null) {
			logger.info("pass" + credential.getPassword());
		}

		byte[] salt = credential.getSalt();
		for(byte b : salt) {
			System.out.println(Integer.toHexString(b));
		}
		String passwordHash = PasswordUtils.calculatePasswordHash(password, salt);
		logger.info("hash " + passwordHash);

		if (passwordHash == null || !passwordHash.equals(credential.getPassword())) {
			throw new NotAuthorizedException("invalid password for user " + username);
		}

		HashMap<String, Object> claims = new HashMap<String, Object>();
		claims.put("sub", username);
		claims.put("name", user.getName());
		claims.put("email", user.getEmail());

		// getUserProfileResponse creates the new token.
		HashMap<String, String> responseMap = authUtil.getUserProfileResponse(claims);

		responseMap.put("mustChangePassword",
				credential.isExpired() == null ? "true" : credential.isExpired().toString());

		logger.info("LOGIN SUCCESS ___ " + user.getEmail() + ":" + user.getUuid().toString()
				+ " ___ Authorization will expire at  ___ " + responseMap.get("expirationDate") + "___");

		return PICSUREResponse.success(responseMap);
	}

	public Response updatePassword(Map<String, String> passwordChangeRequest) {

		logger.info("attempt to update password");
		String username = passwordChangeRequest.get("username");
		String password = passwordChangeRequest.get("oldPass");

		if (username == null || password == null || username.isEmpty() || password.isEmpty())
			throw new ProtocolException("Missing Username or password in request body.");

		// Do we have this user already?
		User user = userRepo.findBySubject(username);
		if (user == null) {
			throw new NotAuthorizedException("No user matching user_id " + username + " present in database");
		}

		Credential credential = user.getCredential();
		logger.info("Stored cred" + credential);
		if (credential != null) {
			logger.info("pass" + credential.getPassword());
		}

		byte[] salt = credential.getSalt();
		String passwordHash = PasswordUtils.calculatePasswordHash(password, salt);
		logger.info("hash " + passwordHash);

		if (passwordHash == null || !passwordHash.equals(credential.getPassword())) {
			throw new NotAuthorizedException("invalid password for user " + username);
		}
		
		

		//update!
		
		String newPass = passwordChangeRequest.get("newPass");

		salt = PasswordUtils.getSalt();
		
		for(byte b : salt) {
			System.out.println(Integer.toHexString(b));
		}
		
		passwordHash = PasswordUtils.calculatePasswordHash(newPass, salt);
		logger.info("hash " + passwordHash);
		
		credential.setPassword(passwordHash);
		credential.setSalt(salt);
		credential.setExpired(false);
		
		List<User> users = new ArrayList<User>();
		users.add(user);
		
		return userService.updateUser(users);

//		return PICSUREResponse.success();
	}
}
