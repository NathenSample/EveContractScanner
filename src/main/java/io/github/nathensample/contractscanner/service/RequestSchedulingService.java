package io.github.nathensample.contractscanner.service;

import io.github.nathensample.contractscanner.controllers.ContractRequestController;
import io.github.nathensample.contractscanner.controllers.RegionalController;
import io.github.nathensample.contractscanner.model.RegionPullJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.swing.plaf.synth.Region;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@Service
public class RequestSchedulingService {
    //Recieves updates when a ESI request finishes
    //Will immediately schedule a new job if the call fails
    // Will respect the expiresWhen if the request is a success

    //When it's created, immediately schedule a request for all configured regions

    @Autowired
    private ContractRequestController contractRequestController;

    private static final int POLLING_TIME = 5000;
    private static DateFormat DF = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ssZ");


    private Map<Integer, RegionPullJob> regionPullJobs = new HashMap<>();

    @PostConstruct
    public void postconstruct()
    {
        //Get configured regions from config
        regionPullJobs.put(10000001, new RegionPullJob(10000001, Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT"))).getTime()));
        regionPullJobs.put(10000036, new RegionPullJob(10000036, Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT"))).getTime()));
        regionPullJobs.put(10000028, new RegionPullJob(10000028, Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT"))).getTime()));

        TimeZone tz = TimeZone.getTimeZone("GMT");
        DF.setTimeZone(tz); // strip timezone
        Timer timer = new Timer();
        timer.schedule(new UpdateTask (), 5000, 5000);
    }

    public void setUpdateWhenForRegion(String updateWhen, int regionId)
    {

        try {
            RegionPullJob pullJob = new RegionPullJob(regionId, DF.parse(updateWhen));
            regionPullJobs.put(regionId, pullJob);
        } catch (ParseException e) {
            //TODO: logical handling
            e.printStackTrace();
        }
    }

    public void addJobs(Map<Integer, RegionPullJob> jobsToAdd)
    {
        regionPullJobs.putAll(jobsToAdd);
    }

    public Map<Integer, RegionPullJob> getRegionPullJobs()
    {
        return regionPullJobs;
    }

    private class UpdateTask extends TimerTask
    {
        private DateFormat DF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        private final Logger logger = LoggerFactory.getLogger(UpdateTask.class);

        //Fairly sure this is a megawasteful loop
        @Override
        public void run() {
            Map<Integer, RegionPullJob> failedJobs = new HashMap<>();
            Date now = Calendar.getInstance(TimeZone.getTimeZone(ZoneId.of("GMT"))).getTime();
            for (RegionPullJob job: getRegionPullJobs().values()) {
                //TODO: Need a way of determining if this succeeds, and removing the task if it does
                if (now.compareTo(job.getRetryAfter()) > 0) {
                    logger.info("Time to request work for region [{}]", job.getRegionId());
                    contractRequestController.performScan(job.getRegionId());
                } else
                {
                    logger.debug("Not to request work for region [{}]", job.getRegionId());
                    failedJobs.put(job.getRegionId(), job);
                }
            }
            addJobs(failedJobs);
        }
    }
}
