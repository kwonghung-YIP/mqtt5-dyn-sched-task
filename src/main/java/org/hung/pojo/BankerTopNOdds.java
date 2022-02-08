package org.hung.pojo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BankerTopNOdds {

	private LocalDateTime updAt;
	private String colSt;
	private Banker[] bk;
	
	@Data
	public static class Banker {
		
		private int bkNo;
		private boolean bkScrSt;
		private boolean bkResSt;
		private TopNCombinationOdds[] bkTopN;
	}
	
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
