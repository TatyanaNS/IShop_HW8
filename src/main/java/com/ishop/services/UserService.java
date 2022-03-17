package com.ishop.services;

import com.ishop.exception.BadResourceException;
import com.ishop.exception.ResourceAlreadyExistsException;
import com.ishop.model.User;
import com.ishop.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

  @Autowired
  UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;

  private boolean existsById(UUID id) {
    return userRepository.existsById(id);
  }

  public User getUser(UUID id) {
    return userRepository.getById(id);
  }

  public List<User> findAll(int pageNumber, int rowPerPage) {
    List<User> users = new ArrayList<>();
    Pageable sortedByIdAsc = PageRequest.of(pageNumber - 1, rowPerPage,
        Sort.by("id").ascending());
    userRepository.findAll(sortedByIdAsc).forEach(users::add);
    return users;
  }

  public User save(User user) throws BadResourceException, ResourceAlreadyExistsException {
    if (!StringUtils.isEmpty(user.getEmail())) {
      if (user.getId() != null && existsById(user.getId())) {
        throw new ResourceAlreadyExistsException("User with id: " + user.getId() + " already exists");
      }
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user);
      } else {
        BadResourceException exc = new BadResourceException("Failed to save user");
        exc.addErrorMessage("User is null or empty");
        throw exc;
      }
  }

  public void update(User user) throws Exception {
    User userDb = userRepository.getById(user.getId());
    if (!StringUtils.isEmpty(user.getEmail())) {
      if (!existsById(user.getId())) {
        throw new Exception("Cannot find User with id: " + user.getId());
      }
      if (StringUtils.hasText(user.getPassword())) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
      }
      user.setPassword(userDb.getPassword());
      userRepository.save(user);
    }
    else {
      BadResourceException exc = new BadResourceException("Failed to save user");
      exc.addErrorMessage("User is null or empty");
      throw exc;
    }
  }

  public void deleteById(UUID id) throws Exception {
    if (!existsById(id)) {
      throw new Exception("Cannot find user with id: " + id);
    }
    else {
      userRepository.deleteById(id);
    }
  }

  public Long count() {
    return userRepository.count();
  }
}