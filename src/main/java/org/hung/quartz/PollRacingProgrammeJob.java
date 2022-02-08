package org.hung.quartz;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.hung.apiwrapper.InfoApiWrapper;
import org.hung.pojo.RaPgm;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class PollRacingProgrammeJob extends QuartzJobBean {
	
	@Autowired
	private InfoApiWrapper infoApi;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private Scheduler scheduler;
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		log.info("Polling for racing programme...");
		
		Date jobStartTime = context.getScheduledFireTime();
		Date jobEndTime = context.getNextFireTime();
		
		LocalDate start = LocalDate.now();
		LocalDate end = start.plus(3,ChronoUnit.DAYS);
		
		Mono<JsonNode> mono = infoApi.racingprogramme("en-us",start, end);
		mono.subscribe(json -> {
			log.info("{}",json);
			try {
				RaPgm[] programmes = objectMapper.treeToValue(json,RaPgm[].class);
				for (RaPgm rp:programmes) {
					log.info("{}",rp);
					if (!"Closed".equalsIgnoreCase(rp.getSt())) {
						for (int raceno=1;raceno<=rp.getNumRa();raceno++) {
							String meetingDate = rp.getMtgDate().substring(0, 10);
							schedNewOddsPushJob(jobStartTime, jobEndTime, meetingDate, rp.getVenCode(), raceno, "WIN");
							schedNewOddsPushJob(jobStartTime, jobEndTime, meetingDate, rp.getVenCode(), raceno, "PLA");
						}
					}
				}
			} catch (JsonProcessingException|IllegalArgumentException e) {
				log.error("",e);
			}
		});
		//log.info("here!");
	}
	
	private void schedNewOddsPushJob(Date jobStartTime,Date jobEndTime, String meetingDate, String venue, int raceno, String betType) {

		//Instant now = Instant.now();

		String triggerName = String.format("%s-odds-push-%s_%s-%s-%tT",betType,meetingDate,venue,raceno,jobStartTime);
		
		SimpleTrigger trigger = TriggerBuilder.newTrigger()
			.withIdentity(triggerName, "push-odds")
			.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(6))
			.startAt(jobStartTime).endAt(jobEndTime)
			.forJob("poll-odds-info-job","push-odds")
			.usingJobData("meetingDate", meetingDate)
			.usingJobData("venue", venue)
			.usingJobData("raceno",raceno)
			.usingJobData("betType",betType)
			.build();
		
		try {
			scheduler.scheduleJob(trigger);
		} catch (SchedulerException e) {
			log.error("error when scheduling new odd push task",e);
		}		
	}

}
