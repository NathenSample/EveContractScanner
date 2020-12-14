package io.github.nathensample.contractscanner.model;

import javax.swing.plaf.synth.Region;
import java.util.Date;

public class RegionPullJob {

    private final int regionId;
    private final Date retryAfter;

    public RegionPullJob(int regionId, Date retryAfter )
    {
        this.regionId = regionId;
        this.retryAfter = retryAfter;
    }

    public int getRegionId() {
        return regionId;
    }

    public Date getRetryAfter() {
        return retryAfter;
    }
}
