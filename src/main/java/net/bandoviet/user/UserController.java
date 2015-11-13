package net.bandoviet.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.validation.Valid;

import net.bandoviet.place.PlaceService;

/**
 * Sign in, sign up.
 * 
 * @author quocanh
 *
 */
@Controller
public class UserController {

  @Autowired
  PlaceService placeService;
  
  @Autowired
  UserService userService;
  
  @Autowired
  UserCreateFormValidator userCreateFormValidator;
  
  /**
   * Homepage
   * 
   * @param model
   *          communication between view and controller.
   * @return the login page if user has not yet connected or index page otherwise.
   */
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String login(Authentication authentication, Map<String, Object> model, @RequestParam Optional<String> error) {
    if (authentication != null) {
      CurrentUser user = (CurrentUser) authentication.getPrincipal();
      if (user != null && user.getRole() == Role.USER) {
        return "redirect:/index";
      }     
    }

    
    model.put("places", placeService.getRandom(20));
    model.put("newuser", new UserCreateForm());
    if (error.isPresent()) {
      model.put("error", error);
    }
    return "login";
  }
  
  /*
   * The hasAuthority() SpEL expression is provided by Spring Security, among others, i.e.:
      
      hasAnyAuthority() or hasAnyRole() ('authority' and 'role' are synonyms in Spring Security lingo!) - checks whether the current user has one of the GrantedAuthority in the list.
      hasAuthority() or hasRole() - as above, but for just one.
      isAuthenticated() or isAnonymous() - whether the current user is authenticated or not.
      isRememberMe() or isFullyAuthenticated() - whether the current user is authenticated by 'remember me' token or not.
   */
  @PreAuthorize("hasAuthority('ADMIN')")
  @RequestMapping(value = "/users", method = RequestMethod.GET)
  public String users(Map<String, Object> model) {
    model.put("users", userService.getAllUsers());
    return "users";
  }

  @InitBinder("newuser")
  public void initBinder(WebDataBinder binder) {
    binder.addValidators(userCreateFormValidator);
  }

  // only user himself can access
  @PreAuthorize("@userService.canAccessUser(principal, #id)")
  @RequestMapping("/user/{id}")
  public ModelAndView getUserPage(@PathVariable Long id) {
    return new ModelAndView("index", "user", userService.getUserById(id)
        .orElseThrow(() -> new NoSuchElementException(String.format("User=%s not found", id))));
  }

  /**
   * Save the new user.
   */
  @RequestMapping(value = "/user/create", method = RequestMethod.POST)
  public String handleUserCreateForm(@Valid @ModelAttribute("newuser") UserCreateForm form,
      BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      return "login";
    }
    try {
      userService.create(form);
    } catch (DataIntegrityViolationException e) {
      if (userService.getUserByEmail(form.getEmail()).isPresent()) {
        bindingResult.reject("email.exists", "Email already exists");
      } else {
        bindingResult.reject("An error happens when creating the new user");
      }
      return "login";
    }
    return "redirect:/index";
  }

}
