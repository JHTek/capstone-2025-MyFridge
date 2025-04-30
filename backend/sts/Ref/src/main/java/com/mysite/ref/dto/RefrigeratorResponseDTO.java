package com.mysite.ref.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefrigeratorResponseDTO {

	private int refrigeratorId;
	private String refrigeratorName;

}
