package com.mysite.ref.refrigerator;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRefCId implements Serializable {
	
	private String userId;
	private int refrigeratorId;
	
}
