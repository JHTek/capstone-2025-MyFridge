package com.mysite.ref.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Recipe {
    @Id
    private String recipeId;

    private String recipeName;

    private String thumbnail;

    private String cookTime;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<IngreList> ingreLists = new ArrayList<>();
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeClass> recipeClasses = new ArrayList<>();
    
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeOrder> recipeOrders = new ArrayList<>();
}
