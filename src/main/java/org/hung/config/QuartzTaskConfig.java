package org.hung.config;

import org.hung.quartz.PollOddsJob;
import org.hung.quartz.PollRacingProgrammeJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class QuartzTaskConfig {
	
	@Bean
	public JobDetail pollRacingProgrammeJobDetail() {
		return JobBuilder.newJob()
			.withIdentity("poll-racing-programme-job","racing-programme")
			.storeDurably(true)
			.ofType(PollRacingProgrammeJob.class)
			.build();
	}
	
	@Bean
	public JobDetail pollOddsInfoJobDetail() {
		return JobBuilder.newJob()
				.withIdentity("poll-odds-info-job","push-odds")
				.storeDurably(true)
				.ofType(PollOddsJob.class)
				.build();
	}
	
	@Bean
	public Trigger pollRacingProgrammeSimpleTrigger() {
		return TriggerBuilder.newTrigger()
			.withIdentity("poll-racing-programme-simple","racing-programme")
			.withSchedule(CronScheduleBuilder.cronSchedule("0 0/2 * ? * *"))
			//.startNow().endAt(Date.from(Instant.now().plus(Duration.ofMinutes(5))))
			.forJob("poll-racing-programme-job", "racing-programme")
			.build();
	}
}
