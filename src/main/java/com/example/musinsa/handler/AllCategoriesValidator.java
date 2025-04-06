package com.example.musinsa.handler;

import com.example.musinsa.model.ProductDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllCategoriesValidator implements ConstraintValidator<AllCategoriesRequired, List<ProductDto.Create.Item>> {

    private static final Set<String> REQUIRED_CATEGORIES = new HashSet<>(Arrays.asList(
            "상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리"
    ));

    @Override
    public boolean isValid(List<ProductDto.Create.Item> items, ConstraintValidatorContext context) {
        if (items == null || items.isEmpty()) {
            return false;
        }

        Set<String> categorySet = new HashSet<>();
        for (ProductDto.Create.Item item : items) {
            categorySet.add(item.getCategory());
        }

        return categorySet.equals(REQUIRED_CATEGORIES);
    }
}
