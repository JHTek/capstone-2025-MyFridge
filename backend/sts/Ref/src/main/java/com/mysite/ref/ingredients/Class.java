package com.mysite.ref.ingredients;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Class {
	@Id
	private int classId;
	
	private String className;
	
	@OneToMany(mappedBy = "type")
	private List<Ingredients> ingredients = new ArrayList<>();;

}
