package org.hung.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class OddsInfo {

	private FullOdds fullodds;
	private TopNOdds topN;
	private BankerTopNOdds bKTopN;
	
}
