package com.mysite.ref.recipe;

import com.mysite.ref.ingredients.ClassEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "RecipeClass")
public class RecipeClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 엔티티 고유 ID

    @ManyToOne
    @JoinColumn(name = "RecipeId", referencedColumnName = "Recipeid")
    private Recipe recipe;  // 레시피와의 관계 (Many-to-One)

    @ManyToOne
    @JoinColumn(name = "ClassId", referencedColumnName = "ClassId")
    private ClassEntity classEntity;  // 클래스와의 관계 (Many-to-One)

}

