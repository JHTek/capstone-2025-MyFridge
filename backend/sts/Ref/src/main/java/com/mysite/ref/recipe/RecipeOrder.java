package com.mysite.ref.recipe;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RecipeOrder")
public class RecipeOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int stepNumber;  // 몇 번째 단계인지

    @Lob
    private String description;  // 요리 설명

    private String photoUrl;  // 해당 단계의 사진 URL

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;  // 어떤 레시피에 속하는지
}
