package com.venus.restapp.request;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Size;

/**
 * Main class representing the user request parameters. This object contains
 * all parameters needed for creating an user. The mandatory parameters will
 * be specified in this class. Where as, the optional parameters will be set
 * in {@link BaseUserRequest}
 * 
 * This class will be used against the validator for validation of user requests.
 * 
 * @author sigabort
 *
 */
public class UserRequest extends BaseUserRequest {

  @NotNull(message = "Username must be supplied")
  @Size(min=1, max=255, message = "Name size must be between 1 and 255")
  private String username;

  public void setUsername(String username) {
    this.username = username;
  }
  
  public String getUsername() {
    return this.username;
  }


}
