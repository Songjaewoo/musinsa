package com.example.musinsa;

import com.example.musinsa.model.Product;
import com.example.musinsa.model.ProductDto;
import com.example.musinsa.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private ResourceLoader resourceLoader;

    private final ProductService productService;

    public DataInitializer(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 초기 데이터 입력을 위한 실행 (resources/initData.txt)
     */
    @Override
    public void run(String... args) throws Exception {
        try {
            ClassPathResource resource = resource = new ClassPathResource("initData.txt");
            InputStreamReader reader = new InputStreamReader(resource.getInputStream(), "UTF-8");
            BufferedReader br = new BufferedReader(reader);
            String line;
            String[] div = {"브랜드", "상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리"};
            while ((line = br.readLine()) != null) {
                String[] lineArr = line.split(" ");
                String brand = "";
                int index = 0;
                ProductDto.Create createProduct = new ProductDto.Create();
                List<ProductDto.Create.Item> items = new ArrayList<>();
                for (String s : lineArr) {
                    if (index == 0) {
                        brand = s;
                        createProduct.setBrand(brand);
                    } else {
                        ProductDto.Create.Item item = new ProductDto.Create.Item();
                        item.setCategory(div[index]);
                        item.setPrice(Integer.parseInt(s));
                        items.add(item);
                        createProduct.setItems(items);
                    }
                    index++;
                }

                productService.createProduct(createProduct);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
