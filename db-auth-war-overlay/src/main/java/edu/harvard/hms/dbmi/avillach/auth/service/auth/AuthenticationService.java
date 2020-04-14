package edu.harvard.hms.dbmi.avillach.auth.service.auth;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import edu.harvard.dbmi.avillach.util.exception.ProtocolException;
import edu.harvard.dbmi.avillach.util.response.PICSUREResponse;
import edu.harvard.hms.dbmi.avillach.auth.data.entity.User;
import edu.harvard.hms.dbmi.avillach.auth.data.repository.RoleRepository;
import edu.harvard.hms.dbmi.avillach.auth.data.repository.UserRepository;
import edu.harvard.hms.dbmi.avillach.auth.rest.UserService;
import edu.harvard.hms.dbmi.avillach.auth.security.PasswordUtils;
import edu.harvard.hms.dbmi.avillach.auth.service.MailService;
import edu.harvard.hms.dbmi.avillach.auth.service.OauthUserMatchingService;
import edu.harvard.hms.dbmi.avillach.auth.service.TOSService;
import edu.harvard.hms.dbmi.avillach.auth.utils.AuthUtils;

/**
 * This class provides authentication functionality. This implements an authenticationService interface
 * in the future to support different modes of authentication.
 *
 * <h3>Thoughts of design</h3>
 * The main purpose of this class is returns a token that includes information of the roles of users.
 */
public class AuthenticationService {
    private Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    @Inject
    OauthUserMatchingService matchingService;

    @Inject
    UserRepository userRepository;

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

    public Response getToken(Map<String, String> authRequest){
    	
    	//authRequest will have username/password
    	
    	// look up user and hashed pw from DB & compare.
    	
    	
    	/*
    	 * throw new NotAuthorizedException("...");
        
        OR 
        
        HashMap<String, Object> claims = new HashMap<String,Object>();
        claims.put("sub", userId);
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        HashMap<String, String> responseMap = authUtil.getUserProfileResponse(claims);
        
		logger.info("LOGIN SUCCESS ___ " + user.getEmail() + ":" + user.getUuid().toString() + " ___ Authorization will expire at  ___ " + responseMap.get("expirationDate") + "___");
		
        return PICSUREResponse.success(responseMap);
    	 */
    	
        String username = authRequest.get("username");
        String password = authRequest.get("password");

        if (username == null || password == null || username.isEmpty() || password.isEmpty())
            throw new ProtocolException("Missing Username or password in request body.");


        //Do we have this user already?
        
        
        User user = userRepository.findBySubject(username);
        if  (user == null){
           throw new NotAuthorizedException("No user matching user_id " + username + " present in database");
        }
        
        logger.info("Stored cred" + user.getCredential());
        if(user.getCredential() != null) {
        	logger.info("pass" + user.getCredential().getPassword());
        }
        
        String passwordHash = PasswordUtils.calculatePasswordHash(password);
        logger.info("hash " + passwordHash);
        
        if(passwordHash == null || !passwordHash.equals(user.getCredential().getPassword())) {
        	throw new NotAuthorizedException("invalid password for user " + username);
        }
        
        HashMap<String, Object> claims = new HashMap<String,Object>();
        claims.put("sub", username);
        claims.put("name", user.getName());
        claims.put("email", user.getEmail());
        //getUserProfileResponse creates the new token.
        
        HashMap<String, String> responseMap = authUtil.getUserProfileResponse(claims);
        
		logger.info("LOGIN SUCCESS ___ " + user.getEmail() + ":" + user.getUuid().toString() + " ___ Authorization will expire at  ___ " + responseMap.get("expirationDate") + "___");
		
        return PICSUREResponse.success(responseMap);
    }

    private Map<String, Object> generateClaims(JsonNode userInfo, String... fields){
        Map<String, Object> claims = new HashMap<>();

        for (String field : fields) {
            JsonNode node = userInfo.get(field);
            if (node != null)
                claims.put(field, node.asText());
        }

        return claims;
    }
}
