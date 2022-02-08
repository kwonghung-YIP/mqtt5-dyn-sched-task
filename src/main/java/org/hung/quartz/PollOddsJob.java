package org.hung.quartz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.hung.apiwrapper.InfoApiWrapper;
import org.hung.pojo.PMPoolWithOdds;
import org.hung.solace.SolaceClient;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solacesystems.jcsmp.BytesMessage;
import com.solacesystems.jcsmp.DeliveryMode;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.JCSMPStreamingPublishEventHandler;
import com.solacesystems.jcsmp.SpringJCSMPFactory;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageProducer;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PollOddsJob extends QuartzJobBean {

	@Autowired
	private InfoApiWrapper infoApi;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private SolaceClient solaceClient;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();
		
		String meetingDate = jobDataMap.getString("meetingDate");
		String venue = jobDataMap.getString("venue");
		int raceno = jobDataMap.getInt("raceno");
		String betType = jobDataMap.getString("betType");
		
		log.info("polling for {} odds for {}_{}-{}...",betType,meetingDate,venue,raceno);
		
		try {
			Mono<JsonNode> mono = infoApi.pmpoolsodds(meetingDate, venue, String.valueOf(raceno), betType);
			mono.subscribe(json -> {
				//log.info("{}",json);
				try {
					//JsonNode targetNode = json.get("ra").get(0).get("pl").get(0).get("oddsInfo");
					JsonNode targetNode = json.at("/ra/0/pl/0");
					if (targetNode.isMissingNode()) {
						//log.info("PMPool is missing : {}...",json);
					} else {
						PMPoolWithOdds pmpool = objectMapper.treeToValue(targetNode,PMPoolWithOdds.class);
						log.info("PMPool: {}",pmpool);
						
						String topicName = String.format("/hkjc/ida/rs/v1/meetings/%s/%s/pool/%s/%s/odds",meetingDate,venue,pmpool.getId(),pmpool.getBTyp());
						topicName = "test/topic";
						Topic topic  = JCSMPFactory.onlyInstance().createTopic(topicName);

						solaceClient.sendTextSMFMessage(topic, objectMapper.writeValueAsString(pmpool));
						
						/*
						try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
							GZIPOutputStream gzipOut = new GZIPOutputStream(byteOut);
							objectMapper.writeValue(gzipOut, pmpool);
							solaceClient.sendBytesSMFMessage(topic, byteOut.toByteArray());
						} catch (IOException e) {
							log.error("", e);
						}
						*/
					}
				} catch (JCSMPException e) {
					log.error("",e);			
				} catch (JsonProcessingException | IllegalArgumentException e) {
					log.error("",e);
				}			
			});			
		} catch (WebClientResponseException we) {
			log.error("{}:{}",we.getRawStatusCode(),we.getMessage());
			log.error("get oddsInfo error...",we);
		}

	}

}
