package com.venus.restapp.response.dto;

import java.util.Date;

import com.venus.model.User;
import com.venus.model.BaseModel;

import org.codehaus.jackson.annotate.JsonWriteNullProperties;

/**
 * This class represents the object which is sent to the client as part of
 * the user response. This object is built using the model object from the
 * DB: {@link User}. 
 *
 * @author sigabort
 *
 */
/* make sure we don't send the null values */
@JsonWriteNullProperties(false)
public class UserDTO implements BaseDTO {
  private String username = null;
  private String email = null;
  private String userId = null;
  private String password = null;
  private String firstName = null;
  private String lastName = null;
  private String gender = null;
  private String url = null;
  private String phone = null;
  private String address1 = null;
  private String address2 = null;
  private String city = null;
  private String country = null;
  private String postalCode = null;
  private String photoUrl = null;
  private Date birthDate = null;
  private Date joinDate = null;
  private Date created = null;
  private Date lastModified = null;

  public String getUsername() {
    return this.username;
  }
  public void setUsername(String username) {
    this.username = username;
  }
  public String getEmail() {
    return email;
  }
  public void setEmail(String email) {
    this.email = email;
  }
  public String getUserId() {
    return userId;
  }
  public void setUserId(String userId) {
    this.userId = userId;
  }
  public String getPassword() {
    return password;
  }
  public void setPassword(String password) {
    this.password = password;
  }
  public String getFirstName() {
    return firstName;
  }
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  public String getLastName() {
    return lastName;
  }
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }
  public String getGender() {
    return gender;
  }
  public void setGender(String gender) {
    this.gender = gender;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public String getPhone() {
    return phone;
  }
  public void setPhone(String phone) {
    this.phone = phone;
  }
  public String getAddress1() {
    return address1;
  }
  public void setAddress1(String address1) {
    this.address1 = address1;
  }
  public String getAddress2() {
    return address2;
  }
  public void setAddress2(String address2) {
    this.address2 = address2;
  }
  public String getCity() {
    return city;
  }
  public void setCity(String city) {
    this.city = city;
  }
  public String getCountry() {
    return country;
  }
  public void setCountry(String country) {
    this.country = country;
  }
  public String getPostalCode() {
    return postalCode;
  }
  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }
  public String getPhotoUrl() {
    return photoUrl;
  }
  public void setPhotoUrl(String photoUrl) {
    this.photoUrl = photoUrl;
  }
  public Date getBirthDate() {
    return birthDate;
  }
  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }
  public Date getJoinDate() {
    return joinDate;
  }
  public void setJoinDate(Date joinDate) {
    this.joinDate = joinDate;
  }
  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }
  public Date getLastModified() {
    return lastModified;
  }
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }

  public UserDTO()  {}

  public UserDTO(String username) {
    this.username = username;
  }

  
  /**
   * Get the {@link UserDTO} object from the {@link User}
   * model object (object from the DB).
   * @param user      The model object 
   * @return The DTO {@link UserDTO object} built using the model object
   */
  public UserDTO getDTO(BaseModel u) {
    User user = (User) u;
    if (user != null) {
      UserDTO dto = new UserDTO(user.getUsername());
      dto.setEmail(user.getEmail());
      dto.setUserId(user.getUserId());
      dto.setPassword(user.getPassword());
      dto.setFirstName(user.getFirstName());
      dto.setLastName(user.getLastName());
      dto.setGender(user.getGender());
      dto.setUrl(user.getUrl());
      dto.setPhone(user.getPhone());
      dto.setAddress1(user.getAddress1());
      dto.setAddress2(user.getAddress2());
      dto.setCity(user.getCity());
      dto.setCountry(user.getCountry());
      dto.setPostalCode(user.getPostalCode());
      dto.setPhotoUrl(user.getPhotoUrl());
      dto.setBirthDate(user.getBirthDate());
      dto.setJoinDate(user.getJoinDate());
      dto.setCreated(user.getCreated());
      dto.setLastModified(user.getLastModified());

      return dto;
    }
    return null;
  }
}
 