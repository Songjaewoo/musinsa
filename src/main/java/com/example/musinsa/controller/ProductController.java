package com.example.musinsa.controller;

import com.example.musinsa.model.ApiResponse;
import com.example.musinsa.model.ProductDto;
import com.example.musinsa.service.ProductService;
import com.example.musinsa.singleton.SimpleStorage;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/test")
    public ResponseEntity<ApiResponse> test() {
        SimpleStorage simpleStorage = SimpleStorage.getInstance();

        System.out.println(simpleStorage.categoryMap);
        System.out.println(simpleStorage.totalPriceSet);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "test"));
    }

    /**
     * 1. 카테고리별 최저가격 브랜드와 상품 가격, 총액 조회
     */
    @GetMapping("/minPrice")
    public ResponseEntity<ApiResponse> minPrice() {
        Map<String, Object> result = productService.getMinPrice();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, result));
    }

    /**
     * 2. 단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회
     */
    @GetMapping("/minPriceBrand")
    public ResponseEntity<ApiResponse> minPriceBrand() {
        Map<String, Object> result = productService.getMinPriceByBrand();

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, result));
    }

    /**
     * 3. 카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회
     */
    @GetMapping("/minMaxPrice")
    public ResponseEntity<ApiResponse> minMaxPrice(@RequestParam(value = "category") String category) {
        Map<String, Object> result = productService.getMinMaxPrice(category);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, result));
    }

    /**
     * 4-1. 브랜드 및 상품 추가
     */
    @PostMapping("/product")
    public ResponseEntity<ApiResponse> createProduct(@Valid @RequestBody ProductDto.Create product) {
        productService.createProduct(product);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "상품 등록 성공"));
    }

    /**
     * 4-2. 상품 가격 수정
     */
    @PutMapping("/price")
    public ResponseEntity<ApiResponse> updatePrice(@Valid @RequestBody ProductDto.Update product) {
        productService.updatePrice(product);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "가격 변경 성공"));
    }

    /**
     * 4-3. 브랜드 삭제
     */
    @DeleteMapping("/brand/{brand}")
    public ResponseEntity<ApiResponse> deleteBrand(@PathVariable("brand") String brand) {
        productService.deleteBrand(brand);

        return ResponseEntity.ok(ApiResponse.success(HttpStatus.OK, "브랜드 삭제 성공"));
    }
}
