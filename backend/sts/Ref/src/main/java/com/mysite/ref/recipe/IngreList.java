package com.mysite.ref.recipe;

import jakarta.persistence.Column;
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
@Table(name = "Ingre_list")
public class IngreList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // 엔티티 고유 ID

    @Column(name = "ingre_name")
    private String ingreName;  // 재료 이름

    @Column(name = "ingre_count")
    private String ingreCount;  // 재료 수량

    @Column(name = "ingre_unit")
    private String ingreUnit;  // 재료 단위

    @ManyToOne
    @JoinColumn(name = "Recipeid", referencedColumnName = "recipeId")
    private Recipe recipe;  // 해당 레시피와의 관계 (Many-to-One)


}
