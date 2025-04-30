package com.mysite.ref.recipe;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeClassRepository extends JpaRepository<RecipeClass, Integer> {
	List<RecipeClass> findByClassEntity_ClassIdIn(Set<Integer> classIds);

}
