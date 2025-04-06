package com.example.musinsa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Persistable;

@Entity
@Data
@IdClass(ProductId.class)
@Table(name = "product")
public class Product implements Persistable<ProductId> {
    @Id
    @Column(name = "brand")
    private String brand;

    @Id
    @Column(name = "category")
    private String category;

    @Column(name = "price")
    private Integer price;

    @Transient
    private boolean isNew = true;

    @Override
    public ProductId getId() {
        return new ProductId(brand, category);
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
