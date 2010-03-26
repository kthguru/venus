package com.venus.restapp.controller;

import org.junit.Assert;

import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.ui.Model;

import static org.springframework.test.web.ModelAndViewAssert.*;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.beans.MutablePropertyValues;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;

import com.venus.restapp.response.UserResponse;
import com.venus.restapp.request.UserRequest;
import com.venus.restapp.request.BaseRequest;
import com.venus.restapp.response.BaseResponse;


/**
 * unit tests for {@link UserController}
 * @author sigabort
 */
public class UserControllerTest extends AbstractControllerTest {

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;
  private static UserController controller;

  /**
   * Create the setup for each request.
   * Calls super() to make sure the appContext is created before creating mock objects.
   * After getting appContext, creates mock request, mock response objects and gets the bean for
   * {@link UserController} class. 
   */
  @Before
  public void setUp() {
    super.setUp();
    /* create mock requests, responses - different for each test */
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();

    if (controller == null) {
      // Get the controller from the context
      controller = appContext.getBean(UserController.class);
    }
  }
  
  /**
   * Clear the security context
   */
  @After
  public void tearDown() {
    SecurityContextHolder.clearContext();
  }

  /** 
   * Test getting users home page
   * @throws Exception
   */
  @Test
  public void testGet() throws Exception {
    request.setMethod(HttpMethod.GET.toString());
    final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    assertViewName(mav, "users/home");
    final UserResponse ur = assertAndReturnModelAttributeOfType(mav, "response", UserResponse.class);
    Assert.assertNotNull("Didn't get the response", ur);
    Assert.assertFalse("The error", ur.getError());
  }

  /** 
   * Test getting user which is not existing
   * @throws Exception
   */
  @Test
  public void testGetNonExistingUser() throws Exception {
    request.setMethod(HttpMethod.GET.toString());
    String name = "tGNEU-" + getRandomString();

    /*
     * XXX: using HandlerAdapter.handle() for GET /username requests is not working
     * The exception is regarding the @PathVariable. 
     * The problem is related to : 
     * http://stackoverflow.com/questions/1401128/how-to-unit-test-a-spring-mvc-controller-using-pathvariable
     * So, using workaround:
     * 
     * calling the method directly using binder to validate the request
     */
    final BaseRequest br = new BaseRequest();
    final WebDataBinder binder = new WebDataBinder(br, "request");
    binder.bind(new MutablePropertyValues(request.getParameterMap()));

    final ModelAndView mav = controller.getUser(name, br, binder.getBindingResult());

    //final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    assertViewName(mav, "users/user");
    
    final BaseResponse resp = assertAndReturnModelAttributeOfType(mav, "response", BaseResponse.class);
    Assert.assertNotNull("Didn't get the response", resp);
    Assert.assertTrue("The error", resp.getError());
    Assert.assertEquals("The error code", new Integer(404), (Integer)resp.getHttpErrorCode());
  }

  /** 
   * Test getting user
   * @throws Exception
   */
  @Test
  public void testGetExistingUser() throws Exception {
    request.setMethod(HttpMethod.GET.toString());
    String name = "tGEU-" + getRandomString();
    
    /* Login in as user who has role 'ROLE_ADMIN' */
    Authentication authRequest = new UsernamePasswordAuthenticationToken("ignored", 
        "ignored", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    SecurityContextHolder.getContext().setAuthentication(authRequest);
    
    /* should be POST method, with uri : /create */
    request.setMethod(HttpMethod.POST.toString());
    request.setRequestURI("/create");

    /* create new request object */
    request.setParameter("username", name);
   
    /* create/update the user now*/
    final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    assertViewName(mav, "users/user");
    final UserResponse ur = assertAndReturnModelAttributeOfType(mav, "response", UserResponse.class);
    Assert.assertNotNull("Didn't get the response", ur);
    Assert.assertFalse("The error", ur.getError());

    /*
     * XXX: using HandlerAdapter.handle() for GET /username requests is not working
     * The exception is regarding the @PathVariable. 
     * The problem is related to : 
     * http://stackoverflow.com/questions/1401128/how-to-unit-test-a-spring-mvc-controller-using-pathvariable
     * So, using workaround:
     * 
     * calling the method directly using binder to validate the request
     */
    final BaseRequest br = new BaseRequest();
    final WebDataBinder binder = new WebDataBinder(br, "request");
    binder.bind(new MutablePropertyValues(request.getParameterMap()));

    final ModelAndView mav1 = controller.getUser(name, br, binder.getBindingResult());

    //final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    assertViewName(mav1, "users/user");
    
    final UserResponse resp = assertAndReturnModelAttributeOfType(mav1, "response", UserResponse.class);
    Assert.assertNotNull("Didn't get the response", resp);
    Assert.assertFalse("The error", resp.getError());
    Assert.assertEquals("The error code", new Integer(200), (Integer)resp.getHttpErrorCode());
    Assert.assertNotNull("The user object of the response", resp.getEntry());
  }

  /**
   * Test creating user with out logged in as admin
   * @throws Exception
   */
  @Test
  public void testCreateUserAsNonAdmin() throws Exception {
    /* should be POST method, with uri : /create */
    request.setMethod(HttpMethod.POST.toString());
    request.setRequestURI("/create");

    /* create new request object */
    request.setParameter("username", getRandomString());
    
    /* try to create user with out any credentials */
    try {
      final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    }
    catch (AuthenticationCredentialsNotFoundException acnf) {
      //Test passed
      return;
    }
    Assert.fail();
  }
  
  /**
   * Try to create user with ROLE_ADMIN priviliges
   * @throws Exception
   */
  @Test
  public void testCreateUserWithCredentials() throws Exception {
    /* Login in as user who has role 'ROLE_ADMIN' */
    Authentication authRequest = new UsernamePasswordAuthenticationToken("ignored", 
        "ignored", AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
    SecurityContextHolder.getContext().setAuthentication(authRequest);
    
    /* should be POST method, with uri : /create */
    request.setMethod(HttpMethod.POST.toString());
    request.setRequestURI("/create");

    String name = "tCUWC-" + getRandomString();
    /* create new request object */
    request.setParameter("username", name);
   
    /* create/update the user now*/
    final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    assertViewName(mav, "users/user");
    final UserResponse ur = assertAndReturnModelAttributeOfType(mav, "response", UserResponse.class);
    Assert.assertNotNull("Didn't get the response", ur);
    Assert.assertFalse("The error", ur.getError());
  }
  
  /**
   * Test creating user with logged in as normal user
   * @throws Exception
   */
  @Test
  public void testCreateUserAsNormalUser() throws Exception {
    /* Login in as user who has role 'ROLE_ADMIN' */
    Authentication authRequest = new UsernamePasswordAuthenticationToken("ignored", 
        "ignored", AuthorityUtils.createAuthorityList("ROLE_USER"));
    SecurityContextHolder.getContext().setAuthentication(authRequest);
    
    /* should be POST method, with uri : /create */
    request.setMethod(HttpMethod.POST.toString());
    request.setRequestURI("/create");

    /* create new request object */
    request.setParameter("username", getRandomString());
    
    /* try to create user with out any credentials */
    try {
      final ModelAndView mav = handlerAdapter.handle(request, response, controller);
    }
    catch (AccessDeniedException ade) {
      //Test passed
      return;
    }
    Assert.fail();
  }
  
}