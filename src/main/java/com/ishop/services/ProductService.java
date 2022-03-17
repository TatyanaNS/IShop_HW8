package com.ishop.services;

import com.ishop.exception.BadResourceException;
import com.ishop.exception.ResourceAlreadyExistsException;
import com.ishop.model.Product;
import com.ishop.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

  @Autowired
  ProductRepository productRepository;

  private boolean existsById(UUID id) {
    return productRepository.existsById(id);
  }

  public List<Product> getAllProducts() {
    return productRepository.findAll();
  }

  public Product getProduct(UUID id) {
    return productRepository.getById(id);
  }

  public Product save(Product product) throws BadResourceException, ResourceAlreadyExistsException {
    if (!StringUtils.isEmpty(product.getName())) {
      if (product.getId() != null && existsById(product.getId())) {
        throw new ResourceAlreadyExistsException("Product with id: " + product.getId() + " already exists");
      }
      return productRepository.save(product);
    } else {
      BadResourceException exc = new BadResourceException("Failed to save user");
      exc.addErrorMessage("Product is null or empty");
      throw exc;
    }
  }

  public void update(Product product) throws Exception {
    productRepository.getById(product.getId());
    if (!StringUtils.isEmpty(product.getName())) {
      if (!existsById(product.getId())) {
        throw new Exception("Cannot find product with id: " + product.getId());
      }
      productRepository.save(product);
    }
    else {
      BadResourceException exc = new BadResourceException("Failed to save product");
      exc.addErrorMessage("Product is null or empty");
      throw exc;
    }
  }

  public void deleteById(UUID id) throws Exception {
    if (!existsById(id)) {
      throw new Exception("Cannot find product with id: " + id);
    }
    else {
      productRepository.deleteById(id);
    }
  }
}