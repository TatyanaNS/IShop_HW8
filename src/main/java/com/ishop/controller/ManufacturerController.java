package com.ishop.controller;

import com.ishop.model.Manufacturer;
import com.ishop.repositories.ManufacturerRepository;
import com.ishop.services.ManufacturerService;
import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manufacturers")
public class ManufacturerController {

  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ManufacturerService service;
  @Autowired
  private ManufacturerRepository manufacturerRepository;

  private List<Manufacturer> init() {
    return service.getAllManufacturers();
  }

  @GetMapping
  public String getAll(Model model) {
    model.addAttribute("manufacturers", init());
    return "manufacturers";
  }

  @GetMapping("/add")
  public String showCreate(Model model) {
    Manufacturer manufacturer = new Manufacturer();
    model.addAttribute("add", true);
    model.addAttribute("manufacturer", manufacturer);
    return "manufacturer";
  }

  @PostMapping(value = "/add")
  public String addManufacturer(Model model, @ModelAttribute("manufacturer") @Valid Manufacturer manufacturer,
                                BindingResult result) {
    model.addAttribute("add", true);
    Manufacturer manufacturerFromDb = manufacturerRepository.findByName(manufacturer.getName());
    try {
      if (result.hasErrors() || manufacturerFromDb != null) {
        if (manufacturerFromDb != null) {
          model.addAttribute("errorUniqueManufacturer", "This manufacturer is exists! Manufacturer must be unique!");
          return "manufacturer";
        }
        return "manufacturer";
      }
      Manufacturer newManufacturer = service.save(manufacturer);
      return "redirect:/manufacturers";
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
//      model.addAttribute("manufacturers", init());
      return "manufacturer";
    }
  }

  @GetMapping("/{id}")
  public String showEdit(@PathVariable UUID id, Model model) {
    model.addAttribute("manufacturer", service.getManufacturer(id));
    return "manufacturer";
  }

  @PostMapping(value = {"/{id}"})
  public String updateManufacturer(Model model, @PathVariable UUID id, @ModelAttribute("manufacturer") @Valid Manufacturer manufacturer,
                                   BindingResult result) {
    model.addAttribute("add", false);
    Manufacturer manufacturerFromDb = manufacturerRepository.findByName(manufacturer.getName());
    try {
      if (result.hasErrors()) {
        return "manufacturer";
      }
      manufacturer.setId(id);
      service.update(manufacturer);
      return "redirect:/manufacturers";
    } catch (Exception ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      return "manufacturer";
    }
  }

  @GetMapping(value = {"/{id}/delete"})
  public String showDeleteManufacturerById(
      Model model, @PathVariable UUID id) {
    Manufacturer manufacturer = null;
    try {
      manufacturer = service.getManufacturer(id);
    } catch (ResourceNotFoundException ex) {
      model.addAttribute("errorMessage", "Manufacturer not found");
    }
    model.addAttribute("allowDelete", true);
    model.addAttribute("manufacturer", manufacturer);
    return "manufacturer-delete";
  }

  @PostMapping(value = {"/{id}/delete"})
  public String deleteManufacturerById(
      Model model, @PathVariable UUID id) {
    try {
      service.deleteById(id);
      return "redirect:/manufacturers";
    } catch (ResourceNotFoundException ex) {
      String errorMessage = ex.getMessage();
      LOGGER.error(errorMessage);
      model.addAttribute("errorMessage", errorMessage);
      return "manufacturer-delete";
    }
  }
}