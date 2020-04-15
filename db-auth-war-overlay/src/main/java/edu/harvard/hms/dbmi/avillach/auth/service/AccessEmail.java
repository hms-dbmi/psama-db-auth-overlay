package edu.harvard.hms.dbmi.avillach.auth.service;

import edu.harvard.hms.dbmi.avillach.auth.JAXRSConfiguration;
import edu.harvard.hms.dbmi.avillach.auth.data.entity.Role;
import edu.harvard.hms.dbmi.avillach.auth.data.entity.User;

import java.util.Set;

/**
 * <p>Provides attributes for generating email notifications.</p>
 */
public class AccessEmail {

    private String systemName = JAXRSConfiguration.systemName;
    private String documentation = null;

    //this can probably be removed? leaving in for now.
    private boolean rolesExists;
    
    private String username;
    private Set<Role> roles;
    private String initialPassword;

    public AccessEmail(User u) {
        this.username = u.getEmail();
        this.roles = u.getRoles();
        this.initialPassword = u.getInitialPassword();
        System.out.println("ACCESS PW: " + initialPassword);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getDocumentation() {
        return documentation;
    }

    public void setDocumentation(String documentation) {
        this.documentation = documentation;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public boolean isRolesExists() {
        return (roles != null) && (!roles.isEmpty());
    }

	public String getInitialPassword() {
		return initialPassword;
	}

	public void setInitialPassword(String initialPassword) {
		this.initialPassword = initialPassword;
	}
}
