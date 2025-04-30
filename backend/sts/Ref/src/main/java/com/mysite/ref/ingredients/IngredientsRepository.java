package com.mysite.ref.ingredients;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientsRepository extends JpaRepository<Ingredients,Integer> {
	List<Ingredients> findByRefrigeratorRefrigeratorId(int refrigeratorId);

	// 유통기한이 특정 기간 내에 있는 재료 조회
	Page<Ingredients> findByRefrigeratorRefrigeratorIdAndExpirationDateBetweenOrderByExpirationDateAsc(
	        Integer refrigeratorId,
	        LocalDate startDate,
	        LocalDate endDate,
	        Pageable pageable
	    );
	
}
