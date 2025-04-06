package com.example.musinsa.repository;

import com.example.musinsa.model.Product;
import com.example.musinsa.model.ProductId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, ProductId> {
    @Query("SELECT p FROM Product p WHERE p.brand = :brand")
    List<Product> findProductByBrand(@Param("brand") String brand);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.brand = :brand")
    Product findProductByCategoryAndBrand(@Param("category") String category, @Param("brand") String brand);

    @Query("SELECT SUM(p.price) FROM Product p WHERE p.brand = :brand")
    int sumProductPriceByBrand(String brand);

    @Modifying
    @Transactional
    @Query("DELETE FROM Product p WHERE p.brand = :brand")
    int deleteByBrand(@Param("brand") String brand);
}
