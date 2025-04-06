package com.example.musinsa.model;

import com.example.musinsa.handler.AllCategoriesRequired;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class ProductDto {

    @Data
    public static class Create {
        @NotBlank(message = "브랜드는 필수 값입니다.")
        private String brand;

        @Valid
        @AllCategoriesRequired
        @NotEmpty(message = "아이템 목록은 필수 값입니다.")
        private List<Item> items;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item {
            @NotBlank(message = "카테고리는 필수 값입니다.")
            private String category;

            @NotNull(message = "가격은 필수 값입니다.")
            @PositiveOrZero(message = "가격은 0 이상 이어야 합니다.")
            private Integer price;
        }
    }

    @Data
    public static class Update {
        @NotBlank(message = "카테고리는 필수 값입니다.")
        private String category;

        @NotBlank(message = "브랜드는 필수 값입니다.")
        private String brand;

        @NotNull(message = "가격은 필수 값입니다.")
        @PositiveOrZero(message = "가격은 0 이상 이어야 합니다.")
        private Integer updatePrice;
    }
}
