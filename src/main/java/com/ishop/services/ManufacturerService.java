package com.ishop.services;

import com.ishop.exception.BadResourceException;
import com.ishop.exception.ResourceAlreadyExistsException;
import com.ishop.model.Manufacturer;
import com.ishop.repositories.ManufacturerRepository;
import java.util.List;
import java.util.UUID;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ManufacturerService {

  @Autowired
  ManufacturerRepository manufacturerRepository;

  private boolean existsById(UUID id) {
    return manufacturerRepository.existsById(id);
  }

  public List<Manufacturer> getAllManufacturers() {
    return manufacturerRepository.findAll();
  }

  public Manufacturer getManufacturer(UUID id) {
    return manufacturerRepository.getById(id);
  }

  public Manufacturer save(Manufacturer manufacturer) throws ResourceAlreadyExistsException, BadResourceException {
    if (!StringUtils.isEmpty(manufacturer.getName())) {
      if (manufacturer.getId() != null && existsById(manufacturer.getId())) {
        throw new ResourceAlreadyExistsException("Manufacturer with id: " + manufacturer.getId() + " already exists");
      }
      return manufacturerRepository.save(manufacturer);
    }
    else {
      BadResourceException exc = new BadResourceException("Failed to save user");
      exc.addErrorMessage("Manufacturer is null or empty");
      throw exc;
    }
  }

  public Manufacturer update(Manufacturer manufacturer) {
    return manufacturerRepository.save(manufacturer);
  }

  public Manufacturer create(Manufacturer manufacturer) throws ResourceAlreadyExistsException {
    Manufacturer roleDb = manufacturerRepository.findByName(manufacturer.getName());
    if (roleDb.getId() != null) {
      throw new ResourceAlreadyExistsException("Manufacturer with id: " + manufacturer.getId() + " already exists");
    }
    return manufacturerRepository.save(manufacturer);
  }

  public void deleteById(UUID id) throws ResourceNotFoundException {
    if (!existsById(id)) {
      throw new ResourceNotFoundException("Cannot find manufacturer with id: " + id);
    }
    else {
      manufacturerRepository.deleteById(id);
    }
  }
}