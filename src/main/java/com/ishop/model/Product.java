package com.ishop.model;

import java.math.BigDecimal;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Getter
@Setter
@ToString
@Table(name = "products")
public class Product {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(
      name = "UUID",
      strategy = "org.hibernate.id.UUIDGenerator"
  )
  @Column(name = "id", updatable = false, nullable = false)
  private UUID id;

  @Size(min = 5, message = "Product should be at least 5 character.")
  @Column(name = "product_name")
  private String name;

  @Column(name = "price")
  private BigDecimal price = BigDecimal.ZERO;

  @NotNull(message = "Product mast have manufacturer!")
  @ManyToOne
  @JoinColumn(name = "manufacturer_id", referencedColumnName = "id")
  private Manufacturer manufacturer;
}