package com.example.musinsa.singleton;

import java.util.*;

public class SimpleStorage {
    private static SimpleStorage instance;

    List<String> mapOrder = Arrays.asList("상의", "아우터", "바지", "스니커즈", "가방", "모자", "양말", "액세서리");

    public Map<String, SortedSet<CustomOrder>> categoryMap;
    public SortedSet<CustomOrder> totalPriceSet;

    private SimpleStorage() {
        categoryMap = new TreeMap<>(Comparator.comparingInt(mapOrder::indexOf));
        totalPriceSet = new TreeSet<>();
    }

    /**
     * 카테고리별 가격, 총 가격 데이터 저장을 위한 sorted 객체
     */
    public static SimpleStorage getInstance() {
        if (instance == null) {
            synchronized (SimpleStorage.class) {
                if (instance == null) {
                    instance = new SimpleStorage();
                }
            }
        }
        return instance;
    }
}