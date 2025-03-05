package com.mysite.ref.ingredients;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepository extends JpaRepository<Ingredients,Integer> {
	List<Ingredients> findByRefrigeratorRefrigeratorId(int refrigeratorId);
	
}
