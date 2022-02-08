package org.hung.pojo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
public class FullOdds {

	private Date updAt;
	private String colSt;
	//private List<CombinationOdds> cmb = new ArrayList<CombinationOdds>();
	private CombinationOdds[] cmb;

	@Data
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	public static class CombinationOdds {
		
		private String cmbStr;
		private String cmbSt;
		private int scrOrd;
		private Boolean hf;
		//private double wP;
		private String odds;
		private Integer oDrp;

	}	
}
