package com.ishop.repositories;

import com.ishop.model.Manufacturer;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, UUID>, JpaSpecificationExecutor<Manufacturer> {
  Manufacturer findByName(String manufacturerName);
}