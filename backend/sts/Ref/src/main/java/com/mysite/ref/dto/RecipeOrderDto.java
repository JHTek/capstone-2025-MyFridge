package com.mysite.ref.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecipeOrderDto {
    private String instruction;
    
    @JsonCreator
    public static RecipeOrderDto fromString(String instruction) {
        RecipeOrderDto dto = new RecipeOrderDto();
        dto.setInstruction(instruction);
        return dto;
    }
    
    public String getInstruction() {
        return instruction;
    }
    
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    
    // 단계 번호 파싱 (예: "1. 설명..." → 1)
    public int parseStepNumber() {
        try {
            if (instruction != null && instruction.matches("^\\d+\\..*")) {
                return Integer.parseInt(instruction.split("\\.")[0].trim());
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }
    
    // 설명 내용 파싱 (예: "1. 설명..." → "설명...")
    public String parseDescription() {
        if (instruction != null && instruction.contains(".")) {
            return instruction.substring(instruction.indexOf(".") + 1).trim();
        }
        return instruction != null ? instruction : "";
    }
}