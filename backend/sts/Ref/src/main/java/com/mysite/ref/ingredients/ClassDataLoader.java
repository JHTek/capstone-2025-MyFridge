package com.mysite.ref.ingredients;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;

@Component
public class ClassDataLoader implements CommandLineRunner {

    private final ClassRepository classRepository;

    public ClassDataLoader(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 이미 데이터가 있으면 초기화 스킵
        if (classRepository.count() > 0) {
            return;
        }

        // resources/ingredients.json 파일 읽기
        InputStream inputStream = new ClassPathResource("ingredient.json").getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        List<IngredientJson> ingredients = objectMapper.readValue(inputStream, new TypeReference<>() {});

        // id와 name만 추출하여 저장
        for (IngredientJson ingredient : ingredients) {
            if (!ingredient.getName().isBlank()) {  // name이 비어있지 않은 경우만 저장
                ClassEntity classEntity = new ClassEntity();
                classEntity.setClassId(ingredient.getId());
                classEntity.setClassName(ingredient.getName());
                classEntity.setCategory(ingredient.getCategory()); 
                classEntity.setShelfLife(ingredient.getShelf_life()); 
                classRepository.save(classEntity);
            }
        }
        System.out.println("✅ Class 초기 데이터 로드 완료!");
    }

    // JSON 필드와 매핑될 DTO (Lombok 사용)
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class IngredientJson {
        private int id;
        private String name;
        private List<String> info;
        private List<String> trim;
        private int shelf_life;
        private String category;   

        // Getters (필요한 필드만)
        public int getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public int getShelf_life() { return shelf_life; }
        public String getCategory() { return category; }
    }
}