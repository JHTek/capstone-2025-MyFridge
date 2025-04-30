package com.mysite.ref.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysite.ref.dto.IngreListDto;
import com.mysite.ref.dto.RecipeJsonDto;
import com.mysite.ref.dto.RecipeOrderDto;
import com.mysite.ref.ingredients.ClassEntity;
import com.mysite.ref.ingredients.ClassRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Component
public class RecipeDataLoader implements CommandLineRunner {

    private final RecipeRepository recipeRepository;
    private final IngreListRepository ingreListRepository;
    private final RecipeClassRepository recipeClassRepository;
    private final RecipeOrderRepository recipeOrderRepository;
    private final ClassRepository classEntityRepository;

    public RecipeDataLoader(RecipeRepository recipeRepository, IngreListRepository ingreListRepository,
                             RecipeClassRepository recipeClassRepository, RecipeOrderRepository recipeOrderRepository,
                             ClassRepository classEntityRepository) {
        this.recipeRepository = recipeRepository;
        this.ingreListRepository = ingreListRepository;
        this.recipeClassRepository = recipeClassRepository;
        this.recipeOrderRepository = recipeOrderRepository;
        this.classEntityRepository = classEntityRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (recipeRepository.count() > 0) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ClassPathResource resource = new ClassPathResource("recipes_file.zip");

        try (InputStream fileInputStream = resource.getInputStream();
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream)) {

            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                if (entry.getName().endsWith(".json")) {
                    try {
                        // JSON 파일 내용을 메모리로 복사
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            baos.write(buffer, 0, len);
                        }

                        // 복사된 데이터를 ObjectMapper로 변환
                        byte[] jsonData = baos.toByteArray();
                        RecipeJsonDto recipe = objectMapper.readValue(jsonData, RecipeJsonDto.class);
                        saveRecipe(recipe);

                    } catch (Exception e) {
                        e.printStackTrace(); // 예외 출력(디버깅에 도움)
                    }
                }
                zipInputStream.closeEntry(); // 이건 여전히 필요
            }
        }
    }




    private void saveRecipe(RecipeJsonDto recipeJson) {
        // Recipe 저장
        Recipe recipe = new Recipe();
        recipe.setRecipeId(recipeJson.getId());
        recipe.setRecipeName(recipeJson.getName());
        recipe.setThumbnail(recipeJson.getThumbnail());
        
        // time 필드 처리 (문자열 → 숫자)
        recipe.setCookTime(recipeJson.getTime()); 
        recipeRepository.save(recipe);

        // IngreList 저장
        for (IngreListDto ingreDto : recipeJson.getIngre_list()) {
            IngreList ingreList = new IngreList();
            ingreList.setIngreName(ingreDto.getIngre_name());
            ingreList.setIngreCount(ingreDto.getIngre_count());
            ingreList.setIngreUnit(ingreDto.getIngre_unit());
            ingreList.setRecipe(recipe);
            ingreListRepository.save(ingreList);
        }

        // RecipeClass 저장
        for (Integer classId : recipeJson.getIngredient_ids()) {
            try {
                ClassEntity classEntity = classEntityRepository.findById(classId)
                        .orElseThrow(() -> new RuntimeException("Class not found: " + classId));
                RecipeClass recipeClass = new RecipeClass();
                recipeClass.setRecipe(recipe);
                recipeClass.setClassEntity(classEntity);
                recipeClassRepository.save(recipeClass);
            } catch (Exception e) {
                
            }
        }

        // RecipeOrder 저장 - 문자열 배열 처리
        List<RecipeOrderDto> recipeOrders = recipeJson.getRecipe();
        List<String> recipeImgs = recipeJson.getRecipe_img();
        
        for (int i = 0; i < recipeOrders.size(); i++) {
            RecipeOrderDto orderDto = recipeOrders.get(i);
            RecipeOrder recipeOrder = new RecipeOrder();
            
            // 문자열에서 단계 번호와 설명 추출
            recipeOrder.setStepNumber(orderDto.parseStepNumber());
            recipeOrder.setDescription(orderDto.parseDescription());
            
            // 해당 단계의 이미지 URL 설정
            if (i < recipeImgs.size()) {
                recipeOrder.setPhotoUrl(recipeImgs.get(i));
            }
            
            recipeOrder.setRecipe(recipe);
            recipeOrderRepository.save(recipeOrder);
        }
    }
}
