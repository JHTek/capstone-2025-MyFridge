package com.mysite.ref.dto;

import java.util.List;

public class RecipeJsonDto {
    private String id;
    private String name;
    private String thumbnail;
    private String url;
    private List<Integer> ingredient_ids;
    private String time;
    private List<IngreListDto> ingre_list;
    private List<RecipeOrderDto> recipe; // 문자열 배열이 자동 변환됨
    private List<String> recipe_img;
    
    // getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public List<Integer> getIngredient_ids() { return ingredient_ids; }
    public void setIngredient_ids(List<Integer> ingredient_ids) { this.ingredient_ids = ingredient_ids; }
    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public List<IngreListDto> getIngre_list() { return ingre_list; }
    public void setIngre_list(List<IngreListDto> ingre_list) { this.ingre_list = ingre_list; }
    public List<RecipeOrderDto> getRecipe() { return recipe; }
    public void setRecipe(List<RecipeOrderDto> recipe) { this.recipe = recipe; }
    public List<String> getRecipe_img() { return recipe_img; }
    public void setRecipe_img(List<String> recipe_img) { this.recipe_img = recipe_img; }
}