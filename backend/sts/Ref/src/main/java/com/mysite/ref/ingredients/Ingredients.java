package com.mysite.ref.ingredients;

import java.time.LocalDate;

import com.mysite.ref.refrigerator.Refrigerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Ingredients {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int ingredientsId;
	
	@Column(name = "ingredients_name")
	private String ingredientsName;
	
	private int quantity;
	
	private LocalDate expirationDate;
	
	private int storageLocation;
	
	@ManyToOne
	@JoinColumn(name = "class_id")
	private Class type;
	
	@ManyToOne
	@JoinColumn(name = "refrigerator_id")
	private Refrigerator refrigerator;

}
