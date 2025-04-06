package com.example.musinsa.unit.controller;

import com.example.musinsa.controller.ProductController;
import com.example.musinsa.model.ProductDto;
import com.example.musinsa.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("카테고리별 최저가격 브랜드와 상품 가격, 총액 조회")
    public void minPrice() throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> products = Stream.of(
                Map.of("카테고리", "상의", "브랜드", "C", "가격", "10,000"),
                Map.of("카테고리", "아우터", "브랜드", "E", "가격", "5,000"),
                Map.of("카테고리", "바지", "브랜드", "D", "가격", "3,000"),
                Map.of("카테고리", "스니커즈", "브랜드", "G", "가격", "9,000"),
                Map.of("카테고리", "가방", "브랜드", "A", "가격", "2,000"),
                Map.of("카테고리", "모자", "브랜드", "D", "가격", "1,500"),
                Map.of("카테고리", "양말", "브랜드", "I", "가격", "1,700"),
                Map.of("카테고리", "액세서리", "브랜드", "F", "가격", "1,900")
        ).collect(Collectors.toList());

        result.put("최저가", products);
        result.put("총액", "34,100");

        when(productService.getMinPrice()).thenReturn(result);

        mockMvc.perform(get("/minPrice"))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.최저가").value(products))
                .andExpect(jsonPath("$.data.총액").value("34,100"));
    }

    @Test
    @DisplayName("단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회")
    void minPriceBrand() throws Exception {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, String>> products = Stream.of(
                Map.of("카테고리", "상의", "가격", "10,100"),
                Map.of("카테고리", "아우터", "가격", "5,100"),
                Map.of("카테고리", "바지", "가격", "3,000"),
                Map.of("카테고리", "스니커즈", "가격", "9,500"),
                Map.of("카테고리", "가방", "가격", "2,500"),
                Map.of("카테고리", "모자", "가격", "1,500"),
                Map.of("카테고리", "양말", "가격", "2,400"),
                Map.of("카테고리", "액세서리", "가격", "2,000")
        ).collect(Collectors.toList());

        result.put("최저가", products);
        result.put("브랜드", "D");
        result.put("총액", "36,100");

        when(productService.getMinPriceByBrand()).thenReturn(result);

        mockMvc.perform(get("/minPriceBrand"))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.최저가").value(products))
                .andExpect(jsonPath("$.data.브랜드").value("D"))
                .andExpect(jsonPath("$.data.총액").value("36,100"));
    }

    @Test
    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회")
    void minMaxPrice() throws Exception {
        String category = "상의";

        Map<String, Object> result = new HashMap<>();
        Map<String, String> minPrice = new HashMap<>();
        minPrice.put("브랜드", "C");
        minPrice.put("가격", "10,000");

        Map<String, String> maxPrice = new HashMap<>();
        maxPrice.put("브랜드", "I");
        maxPrice.put("가격", "11,400");

        result.put("최저가", minPrice);
        result.put("최고가", maxPrice);
        result.put("카테고리", category);

        when(productService.getMinMaxPrice("상의")).thenReturn(result);

        mockMvc.perform(get("/minMaxPrice").param("category", category))
                .andExpect(status().isOk()) // HTTP 200 상태 코드 확인
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.data.최저가").value(minPrice))
                .andExpect(jsonPath("$.data.최고가").value(maxPrice))
                .andExpect(jsonPath("$.data.카테고리").value(category));
    }

    @Test
    @DisplayName("브랜드 및 상품 추가")
    void createProduct() throws Exception {
        ProductDto.Create createProduct = new ProductDto.Create();
        createProduct.setBrand("A");
        List<ProductDto.Create.Item> items = Stream.of(
                new ProductDto.Create.Item("상의", 1000),
                new ProductDto.Create.Item("아우터", 1000),
                new ProductDto.Create.Item("바지", 1000),
                new ProductDto.Create.Item("스니커즈", 1000),
                new ProductDto.Create.Item("가방", 1000),
                new ProductDto.Create.Item("모자", 1000),
                new ProductDto.Create.Item("양말", 1000),
                new ProductDto.Create.Item("액세서리", 1000))
                .collect(Collectors.toList());
        createProduct.setItems(items);

        mockMvc.perform(post("/product")
                        .content(new ObjectMapper().writeValueAsString((createProduct)))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("상품 등록 성공"));
    }

    @Test
    @DisplayName("상품 가격 수정")
    void updatePrice() throws Exception {
        ProductDto.Update updateProduct = new ProductDto.Update();
        updateProduct.setCategory("상의");
        updateProduct.setBrand("A");
        updateProduct.setUpdatePrice(2000);

        mockMvc.perform(put("/price")
                        .content(new ObjectMapper().writeValueAsString((updateProduct)))
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("가격 변경 성공"));
    }

    @Test
    void deleteBrand() throws Exception {
        mockMvc.perform(delete("/brand/A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("브랜드 삭제 성공"));
    }
}