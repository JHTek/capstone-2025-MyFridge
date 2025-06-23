package com.mysite.ref.ingredients;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<ClassEntity, Integer>{
	Optional<ClassEntity> findByClassNameIgnoreCase(String classsName);

}
