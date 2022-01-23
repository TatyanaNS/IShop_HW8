package com.ishop.controller;

import com.ishop.exception.BadResourceException;
import com.ishop.exception.ResourceAlreadyExistsException;
import com.ishop.model.Role;
import com.ishop.model.User;
import com.ishop.repositories.UserRepository;
import com.ishop.services.RoleService;
import com.ishop.services.UserService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@PreAuthorize("hasAuthority('admin')")
@RequestMapping("/users")
public class UserController {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
  private final int ROW_PER_PAGE = 5;
  @Autowired
  private UserService userService;
  @Autowired
  private RoleService roleService;
  @Autowired
  private UserRepository userRepository;

  private List<Role> init() {
    return roleService.getAllRoles();
  }

  @Value("${msg.title}")
  private String title;

  @GetMapping
  public String getUsers(Model model,
      @RequestParam(value = "page", defaultValue = "1") int pageNumber) {
    List<User> users = userService.findAll(pageNumber, ROW_PER_PAGE);
    Long count = userService.count();
    boolean hasPrev = pageNumber > 1;
    boolean hasNext = ((long) pageNumber * ROW_PER_PAGE) < count;
    model.addAttribute("users", users);
    model.addAttribute("hasPrev", hasPrev);
    model.addAttribute("prev", pageNumber - 1);
    model.addAttribute("hasNext", hasNext);
    model.addAttribute("next", pageNumber + 1);
    model.addAttribute("roles", roleService.getAllRoles());
    return "users";
  }

  @GetMapping(value = {"/add"})
  public String showAddUser(@Valid Model model) {
    User user = new User();
    model.addAttribute("add", true);
    model.addAttribute("user", user);
    model.addAttribute("roles", roleService.getAllRoles());
    return "user";
  }

  @PostMapping(value = "/add")
  public String addUser(Model model, @ModelAttribute("user") @Valid User user,
                        BindingResult result) throws BadResourceException, ResourceAlreadyExistsException {
    User userFromDb = userRepository.findUserByEmail(user.getEmail());
    model.addAttribute("add", true);
    model.addAttribute("roles", init());
    if (result.hasErrors() || user.getRoles().size() == 0 || userFromDb != null) {
      if (user.getRoles().size() == 0) {
        model.addAttribute("errorRoles", "User has minimum one role!");
        return "user";
      } else if (userFromDb != null) {
        model.addAttribute("errorUniqueEmail", "This email is exists! Email must be unique!");
        return "user";
      }
      return "user";
    } else {
         userService.save(user);
        return "redirect:/users";
      }
  }

  @GetMapping(value = {"/{id}"})
  public String showEditUser(Model model, @PathVariable UUID id) {
    User user = null;
    model.addAttribute("roles", init());
    try {
      user = userService.getUser(id);
    } catch (ResourceNotFoundException ex) {
      model.addAttribute("errorMessage", "User not found");
    }
    model.addAttribute("add", false);
    model.addAttribute("user", user);
    return "user";
  }

  @PostMapping(value = {"/{userId}"})
  public String updateUser(Model model, @PathVariable UUID userId,
                           @ModelAttribute("user") User user, BindingResult result) {
    model.addAttribute("roles", init());
    try {
      if (result.hasErrors()) {
        return "user";
      } else {
        user.setId(userId);
        userService.update(user);
        return "redirect:/users";
      }
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      model.addAttribute("add", false);
      return "user";
    }
  }

  @GetMapping(value = {"/{id}/delete"})
  public String showDeleteUserById(Model model, @PathVariable UUID id) {
    User user = null;
    model.addAttribute("roles", init());
    try {
        user = userService.getUser(id);
    } catch (ResourceNotFoundException ex) {
      model.addAttribute("errorMessage", "User not found");
    }
    model.addAttribute("allowDelete", true);
    model.addAttribute("user", user);
    return "user-delete";
  }

  @PostMapping(value = {"/{id}/delete"})
  public String deleteUserById(Model model, @PathVariable UUID id) {
    model.addAttribute("roles", init());
    try {
        userService.deleteById(id);
        return "redirect:/users";
    } catch (ResourceNotFoundException ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      return "user-delete";
    }
  }
}