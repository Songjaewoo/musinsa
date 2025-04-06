package com.example.musinsa.service;

import com.example.musinsa.handler.CustomException;
import com.example.musinsa.repository.ProductRepository;
import com.example.musinsa.model.Product;
import com.example.musinsa.model.ProductDto;
import com.example.musinsa.singleton.CustomOrder;
import com.example.musinsa.singleton.SimpleStorage;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Service
public class ProductService {
    SimpleStorage simpleStorage = SimpleStorage.getInstance(); //가격 데이터 sorted 객체
    NumberFormat numberFormat = NumberFormat.getInstance();

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Map<String, Object> getMinPrice() {
        Map<String, Object> result = new HashMap<>();
        ArrayList<Map<String, String>> products = new ArrayList<>();
        AtomicInteger totalPrice = new AtomicInteger();

        if (simpleStorage.categoryMap.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다.");
        }

        simpleStorage.categoryMap.forEach((category, sortedSet) -> {
            Map<String, String> product = new HashMap<>();
            CustomOrder data = sortedSet.first();
            int price = data.getPrice();
            String brand = data.getBrand();

            product.put("카테고리", category);
            product.put("브랜드", brand);
            product.put("가격", numberFormat.format(price));
            products.add(product);
            totalPrice.addAndGet(price);
        });

        result.put("최저가", products);
        result.put("총액", numberFormat.format(totalPrice));

        return result;
    }

    public void createProduct(ProductDto.Create createProduct) {
        String brand = createProduct.getBrand();
        int totalPrice = 0;

        List<ProductDto.Create.Item> items = createProduct.getItems();
        for (ProductDto.Create.Item item : items) {
            String category = item.getCategory();
            int price = item.getPrice();
            String setKey = price + "/" + brand;
            totalPrice += price;

            Product product = new Product();
            product.setBrand(brand);
            product.setCategory(category);
            product.setPrice(price);
            productRepository.save(product);

            simpleStorage.categoryMap.putIfAbsent(category, new TreeSet<>());
            simpleStorage.categoryMap.get(category).add(new CustomOrder(setKey));
        }

        String setKey = totalPrice + "/" + brand;
        simpleStorage.totalPriceSet.add(new CustomOrder(setKey));
    }

    public void deleteBrand(String brand) {
        int totalPrice = 0;
        List<Product> products = productRepository.findProductByBrand(brand);
        if (products.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다.");
        } else {
            for (Product product : products) {
                String category = product.getCategory();
                int price = product.getPrice();
                String key = price + "/" + brand;
                simpleStorage.categoryMap.get(category).remove(new CustomOrder(key));

                totalPrice += price;
            }

            String key = totalPrice + "/" + brand;
            simpleStorage.totalPriceSet.remove(new CustomOrder(key));

            productRepository.deleteByBrand(brand);
        }
    }

    public void updatePrice(ProductDto.Update updateProduct) {
        String category = updateProduct.getCategory();
        String brand = updateProduct.getBrand();
        int updatePrice = updateProduct.getUpdatePrice();

        Product product = productRepository.findProductByCategoryAndBrand(category, brand);
        if (product == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다.");
        }

        int originBrandPrice = productRepository.sumProductPriceByBrand(brand);
        int originPrice = product.getPrice();

        product.setPrice(updatePrice);
        productRepository.save(product);

        String removeKey = originPrice + "/" + brand;
        String addKey = updatePrice + "/" + brand;
        simpleStorage.categoryMap.get(category).remove(new CustomOrder(removeKey));
        simpleStorage.categoryMap.get(category).add(new CustomOrder(addKey));

        int updateBrandPrice = productRepository.sumProductPriceByBrand(brand);
        removeKey = originBrandPrice + "/" + brand;
        addKey = updateBrandPrice + "/" + brand;
        simpleStorage.totalPriceSet.remove(new CustomOrder(removeKey));
        simpleStorage.totalPriceSet.add(new CustomOrder(addKey));
    }

    public Map<String, Object> getMinPriceByBrand() {
        if (simpleStorage.totalPriceSet.isEmpty()) {
            throw new CustomException(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다.");
        }

        Map<String, Object> result = new HashMap<>();

        CustomOrder data = simpleStorage.totalPriceSet.first();
        int minTotalPrice = data.getPrice();
        String brand = data.getBrand();
        int calcTotalPrice = 0;

        List<Map<String, Object>> infoList = new ArrayList<>();
        List<Product> products = productRepository.findProductByBrand(brand);
        for (Product product : products) {
            String category = product.getCategory();
            int price = product.getPrice();

            Map<String, Object> info = new HashMap<>();
            info.put("카테고리", category);
            info.put("가격", numberFormat.format(price));
            infoList.add(info);

            calcTotalPrice += price;
        }
        result.put("최저가", infoList);
        result.put("브랜드", brand);
        result.put("총액", numberFormat.format(minTotalPrice));

        return result;
    }

    public Map<String, Object> getMinMaxPrice(String category) {
        if (simpleStorage.categoryMap.isEmpty() || simpleStorage.categoryMap.get(category) == null) {
            throw new CustomException(HttpStatus.NOT_FOUND, "데이터가 존재하지 않습니다.");
        }

        Map<String, Object> result = new HashMap<>();

        SortedSet<CustomOrder> sortedSet = simpleStorage.categoryMap.get(category);
        CustomOrder firstData = sortedSet.first();
        int minPrice = firstData.getPrice();
        String minBrand = firstData.getBrand();

        Map<String, Object> minMap = new HashMap<>();
        minMap.put("브랜드", minBrand);
        minMap.put("가격", numberFormat.format(minPrice));

        CustomOrder lastData = sortedSet.last();
        int maxPrice = lastData.getPrice();
        String maxBrand = lastData.getBrand();

        Map<String, Object> maxMap = new HashMap<>();
        maxMap.put("브랜드", maxBrand);
        maxMap.put("가격", numberFormat.format(maxPrice));

        result.put("카테고리", category);
        result.put("최저가", minMap);
        result.put("최고가", maxMap);

        return result;
    }
}
