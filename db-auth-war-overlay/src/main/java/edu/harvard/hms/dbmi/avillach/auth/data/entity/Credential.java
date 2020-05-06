package edu.harvard.hms.dbmi.avillach.auth.data.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;

import edu.harvard.dbmi.avillach.data.entity.BaseEntity;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity(name = "credential")
public class Credential extends BaseEntity implements Serializable {

	/**
	 * generated
	 */
	private static final long serialVersionUID = -4652918137080056344L;

	/**
	 * should be stored as a salted hash
	 */
	@Column(name = "password")
	@Type(type = "text")
	private String password;
	
	@Column(name = "created_on")
	@Type(type = "date")
	private Date createdOn;
	
	
	@Column(name = "salt", columnDefinition = "BINARY(16)")
	private byte[] salt;
	
	@Column(name = "is_expired")
	private Boolean isExpired;

	
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public Boolean isExpired() {
		return isExpired;
	}

	public void setExpired(Boolean isExpired) {
		this.isExpired = isExpired;
	}
	
	
	
}
