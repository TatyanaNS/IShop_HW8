package com.ishop.controller;

import com.ishop.model.Manufacturer;
import com.ishop.model.Product;
import com.ishop.repositories.ProductRepository;
import com.ishop.services.ManufacturerService;
import com.ishop.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ProductService service;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  ManufacturerService manufacturerService;

  private List<Manufacturer> init() {
    return manufacturerService.getAllManufacturers();
  }

  private List<Product> allProducts() {
    return service.getAllProducts();
  }

  @GetMapping
  public String getAll(Model model) {
    model.addAttribute("products", allProducts());
    model.addAttribute("manufacturers", init());
    return "products";
  }

  @GetMapping("/add")
  public String showCreate(Model model) {
    model.addAttribute("manufacturers", init());
    Product product = new Product();
    model.addAttribute("add", true);
    model.addAttribute("product", product);
    return "product";
  }

  @PostMapping(value = "/add")
  public String addProduct(Model model, @ModelAttribute("product") @Valid Product product,
                           BindingResult result) {
    try {
      model.addAttribute("add", true);
      model.addAttribute("manufacturers", init());
      Product productFromDb = productRepository.findByName(product.getName());
      Manufacturer manufacturer = product.getManufacturer();
      if (result.hasErrors() || productFromDb != null || manufacturer == null) {
        if (productFromDb != null) {
          model.addAttribute("errorUniqueProduct", "This product is exists! Product must be unique!");
          return "product";
        } else if (manufacturer == null) {
          model.addAttribute("errorManufacturer", "Product must have manufacturer!");
          return "product";
        }
        return "product";
      } else {
        service.save(product);
        return "redirect:/products";
      }
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
    }
    return "product";
  }

  @GetMapping("/{id}")
  public String showEdit(@PathVariable UUID id, Model model) {
    model.addAttribute("manufacturers", init());
    model.addAttribute("product", service.getProduct(id));
    return "product";
  }

  @PostMapping(value = {"/{id}"})
  public String updateProduct(Model model, @PathVariable UUID id, @ModelAttribute("product") @Valid Product product,
                              BindingResult result) {
    model.addAttribute("add", false);
    model.addAttribute("manufacturers", init());
    Manufacturer manufacturer = product.getManufacturer();
    try {
      if (result.hasErrors() || manufacturer == null) {
        if (manufacturer == null) {
          model.addAttribute("errorManufacturer", "Product must have manufacturer!");
          return "product";
        }
        return "product";
      } else {
        product.setId(id);
        service.update(product);
        return "redirect:/products";
      }
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      return "product";
    }
  }

  @GetMapping(value = {"/{id}/delete"})
  public String showDeleteProductById(
      Model model, @PathVariable UUID id) {
    model.addAttribute("manufacturers", init());
    Product product = null;
    try {
      product = service.getProduct(id);
    } catch (Exception ex) {
      model.addAttribute("errorMessage", "Product not found");
    }
    model.addAttribute("allowDelete", true);
    model.addAttribute("product", product);
    return "product-delete";
  }

  @PostMapping(value = {"/{id}/delete"})
  public String deleteProductById(
      Model model, @PathVariable UUID id) {
    model.addAttribute("manufacturers", init());
    try {
      service.deleteById(id);
      return "redirect:/products";
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      return "product-delete";
    }
  }
}