package com.leanx.app.service.jobs.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.leanx.app.service.jobs.OnboardingJob;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener 
public class JobScheduleInitializer implements ServletContextListener {

    private static final Logger logger = Logger.getLogger(JobScheduleInitializer.class.getName());

    private Scheduler scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            // Initialize the Quartz Scheduler
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            logger.info("JobScheduler started...");

            // Define Job
            JobDetail job = JobBuilder.newJob(OnboardingJob.class)
                    .withIdentity("onboardingJob", "hr-ops")
                    .build();

            // Define Trigger (Runs every day at 1:00 AM)
            /* Cron Expressions: 
                *  *  *  *  *  *  *
                |  |  |  |  |  |  |-- Year (optional)
                |  |  |  |  |  +----- Day of the Week (1-7 or SUN-SAT)
                |  |  |  |  +-------- Month (0-11 or JAN-DEC)
                |  |  |  +----------- Day of the Month (1-31)
                |  |  +-------------- Hour (0-23)
                |  +----------------- Minute (0-59)
                +-------------------- Second (0-59)    

                ? <-- wildcard since one field is definied that makes other field obsolete
                examples:
                0 * * * * ? --> Triggers every minute
                0 0 * * * ? --> Triggers every hour
                0 0 1 * * ? --> Triggers every day at 1:00 AM
                0 0/15 * * ? --> Triggers every 15 minutes
            */ 
            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("everyMinuteTrigger", "hr-ops")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/15 * * * ?"))
                    .build();

            // Schedule the job
            scheduler.scheduleJob(job, trigger);
            logger.info("OnboardingJob scheduled successfully with trigger: dailyTrigger");
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Error starting job scheduler!", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                logger.info("JobScheduler stopped...");
            }
        } catch (SchedulerException e) {
            logger.log(Level.SEVERE, "Error shutting down job scheduler!", e);
        }
    }
}