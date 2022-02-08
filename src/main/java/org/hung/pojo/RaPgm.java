package org.hung.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RaPgm {
	
	private String id;
	private String mtgDate;
	private String venCode;
	private String st;
	private int numRa;
	private String no;
	private String dOW;
	private String mtgTyp;
	private String mtgTypDes;
	
}
