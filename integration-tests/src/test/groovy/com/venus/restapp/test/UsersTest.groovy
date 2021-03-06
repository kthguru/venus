package com.venus.restapp.test;

import com.venus.restapp.VenusRestJSONClient;

import org.junit.Before;
import org.junit.Test;
import org.apache.http.HttpStatus;
import org.junit.Assert;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Date;

/**
 * Class for tests related to user
 */
public class UsersTest extends AbstractTest {

  private VenusRestJSONClient client = null;
  
  @Before
  public void setUp() {
    client = new VenusRestJSONClient('UTest-' + getRandomString());
  }

  /**
   * Test to check whether an admin user can create the user or not
   */
  @Test
  public void testCreateUser() {
    createAdminUserAndLogin(client);
    
    def name = "testCU-" + getRandomString();
    def password = name + "-passwd";
    def params = [password:password];
	
    def resp = client.createUser(name, params);
    Assert.assertFalse("Can't create user", resp?.error);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);

    /* logout now and try to crete */
    client.logout();

    /* check the error creating user with out credentials */
    resp = client.createUser(name, params);
    testError(resp, HttpStatus.SC_UNAUTHORIZED);
  }

  /**
   * Create user as admin, get the user and check the user info
   */
  @Test
  public void testGetUserAsAdmin() {    
    /* create an admin user and login as admin */
    createAdminUserAndLogin(client);
    def name = "testGU-" + getRandomString();
    def params = buildUserOptionalParams(name);

    /* create the user and check the details */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);
    
    /* get the created user info now */
    resp = client.getUser(name, null);
    testNoErrors(resp);
    user = resp?.entry;
    testUserDetails(user, params);
  }

  /**
   * Create user as admin, get the user as not logged in user and check the user info
   * For now, our server sends all information regarding to user. This test needs to be
   * changed when our server sends mini-info for not-logged in user
   */
  @Test
  public void testGetUserAsAnonymousUser() {    
    /* create an admin user and login as admin */
    createAdminUserAndLogin(client);
    def name = "testGUAAnonU-" + getRandomString();
    def params = buildUserOptionalParams(name);

    /* create the user and check the details */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);

    /* logout now */
    resp = client.logout();
    testNoErrors(resp);
       
    /* get the created user info now as anonymous user */
    resp = client.getUser(name, null);
    testNoErrors(resp);
    user = resp?.entry;
    testUserDetails(user, params);
  }
  
  /**
   * Create the user and login as the created user
   */
  @Test
  public void testCreateNormalUserAndLogin() {    
    /* create an admin user and login as admin */
    createAdminUserAndLogin(client);
    def name = "testCNUAL-" + getRandomString();
    def params = buildUserOptionalParams(name);

    /* create the user and check the details */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);

    /* logout now */
    resp = client.logout();
    testNoErrors(resp);

    /* login as the newly created user */
    resp = client.login(params['username'], params['password'], null);
    testNoErrors(resp);

    /* get my details */
    resp = client.getUser(name, null);
    testNoErrors(resp);
    user = resp?.entry;
    testUserDetails(user, params);
    
    /* logout now */
    resp = client.logout();
    testNoErrors(resp);    
  }
  
  /**
   * Try to create a user with out logging in
   */
  @Test
  public void testCreateUserAsNotLoggedInUser() {
    def name = "testCUANIU-" + getRandomString();
    def params = buildUserOptionalParams(name);

    /* try to create the user */
    def resp = client.createUser(name, params);
    testError(resp, HttpStatus.SC_UNAUTHORIZED); // expected : unauthorized(401)
  }
  
  /**
   * Try to create a user with normal user
   */
  @Test
  public void testCreateUserAsNormalUser() {
    def name = "testCUANU-" + getRandomString();
    createAdminUserAndLogin(client);
    def user = createTestUser(client, name);
    /* log out as admin */
    def resp = client.logout();  
    testNoErrors(resp);

    resp = client.login(user?.username, user?.password, null);
    testNoErrors(resp);
 
    def name1 = name + "-1";
    def params1 = buildUserOptionalParams(name1);
    
    /* try to create the user now */
    resp = client.createUser(name1, params1);
    testError(resp, HttpStatus.SC_FORBIDDEN); // expected: access denied (403)
  }

  /**
   * Try to create a user with no username
   */
  @Test
  public void testCreateUserWithNoUsername() {
    def name = "testCUWWI-" + getRandomString();
    createAdminUserAndLogin(client);
        
    def params = buildUserOptionalParams(name);
    
    /* try to create the user with out username */
    def resp = client.createUser(null, params);
    testError(resp, HttpStatus.SC_BAD_REQUEST); // expected: bad request (400)
  }

  /**
   * Try to create a user with already used email
   */
  @Test
  public void testCreateUserWithAlreadyUsedEmail() {
    def name = "testCUAUE-" + getRandomString();
    createAdminUserAndLogin(client);

    def params = buildUserOptionalParams(name);
    
    /* create one user with one email */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    
    /* try to create a different user with same email */
    def name1 = name + "-1";
    params['userId'] = name1;
    resp = client.createUser(name1, params);
    testError(resp, HttpStatus.SC_BAD_REQUEST); // expected: bad request (400)    
  }

  /**
   * Try to create a user with already used userId
   */
  @Test
  public void testCreateUserWithAlreadyUsedUserId() {
    def name = "testCUAUUId-" + getRandomString();
    createAdminUserAndLogin(client);

    def params = buildUserOptionalParams(name);
    
    /* create one user with one userId */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    
    /* try to create a different user with same userId */
    def name1 = name + "-1";
    params['email'] = name1+"@gmail.com";
    resp = client.createUser(name1, params);
    testError(resp, HttpStatus.SC_BAD_REQUEST); // expected: bad request (400)    
  }

  /**
   * Create user and update the details
   */
  @Test
  public void testUpdateUser() {    
    /* create an admin user and login as admin */
    createAdminUserAndLogin(client);
    def name = "testUU-" + getRandomString();
    def params = buildUserOptionalParams(name);

    /* create the user and check the details */
    def resp = client.createUser(name, params);
    testNoErrors(resp);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);

    /* update the same user with different optional params */
    def newName = name + "-1";
    params = buildUserOptionalParams(newName);
    
    /* get the created user info now */
    resp = client.createUser(name, params);
    testNoErrors(resp);
    user = resp?.entry;
    params['username'] = name;
    /* the other values should be updated */
    testUserDetails(user, params);
  }
  
  
  /**
   * Create one test user and return user
   * This can be used by other tests to create a user quickly
   */
  public static Object createTestUser(myClient, name) {
    def params = buildUserOptionalParams(name);
  
    /* create user */
    def resp = myClient.createUser(name, params);
    testNoErrors(resp);
    def user = resp?.entry;
    params['username'] = name;
    testUserDetails(user, params);

    return user;
  }
  
  
  /**
   * create optional parameters for creating user, and build a map of the optional params
   * and return. The optional parameters are based on the 'name' sent as argument
   */
  public static Map buildUserOptionalParams(name) {
    def password = name + "-passwd";
    def userId = name + "-userId";
    def email = "sigabort@email.com";
    def firstName = name + "-firstName";
    def lastName = name + "-lastName";
    def gender = 'male';
    def address1 = name + "-address1";
    def address2 = name + "-address2";
    def city = name + "-city";
    def phone = '88789889988';
    def country = name + "-cntry";
    def postalCode = '121121';
    def url = 'http://www.linked.in/sigabort';
    def photoUrl = 'http://www.linked.in/sigabort/pic';
    def joinDate = '11/11/2009';
    def birthDate = '06/06/1990';
    
    return [password:password, userId:userId, email:email, firstName:firstName, lastName:lastName, 
            gender:gender, address1:address1, address2:address2, city:city, country:country, phone:phone,
            postalCode:postalCode, url:url, photoUrl:photoUrl, joinDate:joinDate, birthDate:birthDate];
  }

  /**
   * Test the user details returned from the server
   */
  public static void testUserDetails(def user, def params) {
    if (user == null) {
      return;
    }
    Assert.assertNotNull("The user is not proper", user);
    if (params == null) {
      return;
    }
    Assert.assertEquals("The user's username", params['username'], user?.username);
    Assert.assertEquals("The user's password", params['password'], user?.password);
    Assert.assertEquals("The user's userId", params['userId'], user?.userId);
    Assert.assertEquals("The user's email", params['email'], user?.email);
    Assert.assertEquals("The user's firstName", params['firstName'], user?.firstName);
    Assert.assertEquals("The user's lastName", params['lastName'], user?.lastName);
    Assert.assertEquals("The user's gender", params['gender'], user?.gender);
    Assert.assertEquals("The user's url", params['url'], user?.url);
    Assert.assertEquals("The user's phone", params['phone'], user?.phone);
    Assert.assertEquals("The user's address1", params['address1'], user?.address1);
    Assert.assertEquals("The user's address2", params['address2'], user?.address2);
    Assert.assertEquals("The user's city", params['city'], user?.city);
    Assert.assertEquals("The user's country", params['country'], user?.country);
    Assert.assertEquals("The user's postalCode", params['postalCode'], user?.postalCode);
    Assert.assertEquals("The user's photoUrl", params['photoUrl'], user?.photoUrl);
    SimpleDateFormat df = new SimpleDateFormat('dd/MM/yyyy');

    /* server sends the dates in unix epoch timings, so convert them to client's style before
     * testing
     */
    if (user?.joinDate != null) {
      user.joinDate = df.format(new Date(user?.joinDate));
    }
    if (user?.birthDate != null) {
      user.birthDate = df.format(new Date(user?.birthDate));
    }
    
    Assert.assertEquals("The user's joinDate", params['joinDate'], user?.joinDate);
    Assert.assertEquals("The user's birthDate", params['birthDate'], user?.birthDate);
    
  }
    
}