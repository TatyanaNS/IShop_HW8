package com.ishop.services;

import com.ishop.exception.BadResourceException;
import com.ishop.exception.ResourceAlreadyExistsException;
import com.ishop.model.Role;
import com.ishop.repositories.RoleRepository;
import java.util.List;
import java.util.UUID;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class RoleService {

  @Autowired
  RoleRepository roleRepository;

  private boolean existsById(UUID id) {
    return roleRepository.existsById(id);
  }

  public List<Role> getAllRoles() {
    return roleRepository.findAll();
  }

  public Role getRole(UUID id) {
    return roleRepository.getById(id);
  }

  public Role save(Role role) throws ResourceAlreadyExistsException, BadResourceException {
    if (!StringUtils.isEmpty(role.getName())) {
      if (role.getId() != null && existsById(role.getId())) {
        throw new ResourceAlreadyExistsException("Role with id: " + role.getId() +
            " already exists");
      }
      return roleRepository.save(role);
    }
    else {
      BadResourceException exc = new BadResourceException("Failed to save user");
      exc.addErrorMessage("Role is null or empty");
      throw exc;
    }
  }

  public Role update(Role role) {
    return roleRepository.save(role);
  }

  public Role create(Role role) throws ResourceAlreadyExistsException {
    Role roleDb = roleRepository.findByName(role.getName());
    if (roleDb.getId() != null) {
      throw new ResourceAlreadyExistsException("Role with id: " + role.getId() + " already exists");
    }
    return roleRepository.save(role);
  }

  public void deleteById(UUID id) throws ResourceNotFoundException {
    if (!existsById(id)) {
      throw new ResourceNotFoundException("Cannot find role with id: " + id);
    }
    else {
      roleRepository.deleteById(id);
    }
  }
}