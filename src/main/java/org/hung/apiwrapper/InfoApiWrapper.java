package org.hung.apiwrapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class InfoApiWrapper {
	
	@Autowired
	@Qualifier("infoapi-webclient")
	private WebClient webclient;
	
	private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	public Mono<JsonNode> operatingcontrol() {

		return webclient.get()
				.uri("/operatingcontrol", builder -> 
				  builder
				    .queryParam("caller", "RacingTouch")
				    .build()
				)
				.header(HttpHeaders.ACCEPT_ENCODING,"gzip")
				.header("X-Request-Id", UUID.randomUUID().toString())
				.retrieve()
				.bodyToMono(JsonNode.class);
	}

	public Mono<JsonNode> racingprogramme(String lang,LocalDate start,LocalDate end) {
		final String lang2 = (lang==null)?"en-us":lang;
		final String start2 = (start!=null)?start.format(dateFormat):null;
		final String end2 = (end!=null)?end.format(dateFormat):null;
		
		return webclient.get()
				.uri("/racingprogramme", builder -> 
				  builder
				    .queryParam("caller", "RacingTouch")
				    .queryParam("language", lang2)
				    //.queryParam("range", "date")
				    //.queryParam("start", start.format(dateFormat))
				    //.queryParam("end", end.format(dateFormat))
				    .build()
				)
				.header(HttpHeaders.ACCEPT_ENCODING,"gzip")
				.header("X-Request-Id", UUID.randomUUID().toString())
				.retrieve()
				.bodyToMono(JsonNode.class);
	}
	
	public Mono<JsonNode> pmpoolsodds(String meetingdate,String venue,String raceno,String betType) {
		return webclient.get()
				.uri("/pmpools/odds", builder -> 
				  builder
				    .queryParam("caller", "RacingTouch")
				    .queryParam("meetingdate", meetingdate)
				    .queryParam("venue", venue)
				    .queryParam("raceno", raceno)
				    .queryParam("bettypes", betType)
				    .build()
				)
				.header(HttpHeaders.ACCEPT_ENCODING,"gzip")
				.header("X-Request-Id", UUID.randomUUID().toString())
				.retrieve()
				.bodyToMono(JsonNode.class);	
	}
}
