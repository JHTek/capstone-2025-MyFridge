package com.mysite.ref.dto;

import com.mysite.ref.recipe.RecipeOrder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeOrderDto {

    private String instruction;     // 예전 방식용
    private int stepNumber;         // 새 필드
    private String description;     // 새 필드
    private String photoUrl;        // 새 필드

    // ✅ RecipeOrder → DTO 변환 (Spring에서 사용)
    public static RecipeOrderDto from(RecipeOrder order) {
        RecipeOrderDto dto = new RecipeOrderDto();
        dto.stepNumber = order.getStepNumber();
        dto.description = order.getDescription();
        dto.photoUrl = order.getPhotoUrl();

        // instruction은 기존 파싱 메서드를 쓰고 싶은 경우를 위해 포맷 생성
        dto.instruction = order.getStepNumber() + ". " + order.getDescription();

        return dto;
    }

    // ✅ 기존 방식 유지 (문자열만 있을 때 파싱 가능)
    public int parseStepNumber() {
        try {
            if (instruction != null && instruction.matches("^\\d+\\..*")) {
                return Integer.parseInt(instruction.split("\\.")[0].trim());
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public String parseDescription() {
        if (instruction != null && instruction.contains(".")) {
            return instruction.substring(instruction.indexOf(".") + 1).trim();
        }
        return instruction != null ? instruction : "";
    }
}
