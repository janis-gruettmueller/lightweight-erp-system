package com.leanx.app.service.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class JobScheduler {
    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        scheduler.start();

        // Define Job
        JobDetail job = JobBuilder.newJob(OnboardingJob.class)
                .withIdentity("onboardingJob", "hr-ops")
                .build();

        // Define Trigger (Runs every 30 seconds)
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("dailyTrigger", "hr-ops")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/30 * * * * ?")) // Every 30 seconds
                .build();

        // Schedule the job
        scheduler.scheduleJob(job, trigger);
    }
}
