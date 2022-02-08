package org.hung.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PMPoolWithOdds {

	private String id;
	private String st;
	private String plStUpdAt;
	private String bTyp;
	private int untBet;
	private boolean aUEnbSt;
	
	private OddsInfo oddsInfo;

}
