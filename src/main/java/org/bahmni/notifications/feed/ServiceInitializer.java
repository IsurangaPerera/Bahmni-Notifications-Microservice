package org.bahmni.notifications.feed;

import org.apache.log4j.BasicConfigurator;
import org.bahmni.notifications.feed.job.BahmniFeedReaderJob;
import org.bahmni.notifications.feed.job.openmrs.OpenMRSPatientFeedReaderJob;
import org.quartz.JobExecutionException;

public class ServiceInitializer {

    public static void main(String[] args) {
        BasicConfigurator.configure();

        BahmniFeedReaderJob j = new OpenMRSPatientFeedReaderJob();
        try {
            j.execute(null);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }
    }
}
