package com.mysite.ref.refrigerator;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Refrigerator {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int refrigeratorId;
	
	@Column(name = "refrigerator_name")
	private String refrigeratorName;

    @OneToMany(mappedBy = "refrigerator")
    private List<UserRefC> userRefC = new ArrayList<>();
    
}
