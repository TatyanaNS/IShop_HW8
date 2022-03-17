package com.ishop.controller;

import com.ishop.model.Role;
import com.ishop.model.User;
import com.ishop.repositories.RoleRepository;
import com.ishop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RegistrationController {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private List<Role> initRoles() {
    return roleRepository.findAll();
  }

  private List<Role> getRoleByDefault() {
    return roleRepository.findAll().stream()
            .filter(roleDefault -> roleDefault.getName().equals("user"))
            .collect(Collectors.toList());
  }

  @GetMapping("/registration")
  public String registration(Model model) {
    model.addAttribute("user", new User());
    model.addAttribute("getRoles", initRoles());
    return "registration";
  }

  @PostMapping("/registration")
  public String registrationUser(@Valid @ModelAttribute User user,
                                  BindingResult result,
                                  Model model) {

    User userFromDb = userRepository.findUserByEmail(user.getEmail());
    model.addAttribute("getRoles", initRoles());
    if (result.hasErrors() || user.getRoles() == null || userFromDb != null) {
      model.addAttribute("message", "Something wrong! Errors: " + result.getFieldErrors().size());
      result
          .getFieldErrors()
          .forEach(f -> model.addAttribute(f.getField(), f.getDefaultMessage()));

//      result
//          .getFieldErrors()
//          .forEach(f -> System.out.println(f.getField() + ": " + f.getDefaultMessage()));
      if (user.getRoles() == null) {
        model.addAttribute("errorRoles", "User has minimum one role!");
        return "registration";
      } else if (userFromDb != null) {
        model.addAttribute("errorUniqueEmail", "This email is exists! Email must be unique!");
        return "registration";
      }
      return "registration";
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setRoles(getRoleByDefault());
    userRepository.save(user);
    return "redirect:/login";
  }
}