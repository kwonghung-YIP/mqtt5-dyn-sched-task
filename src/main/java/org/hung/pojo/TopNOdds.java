package org.hung.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TopNOdds {

	private LocalDateTime updAt;
	private String colSt;
	private TopNCombinationOdds[] cmb;
	
	@Data
	public static class TopNCombinationOdds {
		
		private String cmbStr;
		private String cmbSt;
		private int scrOrd;
		private boolean hf;
		private double wP;
		private String odds;
		
	}
}
