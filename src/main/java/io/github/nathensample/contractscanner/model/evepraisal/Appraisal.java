
package io.github.nathensample.contractscanner.model.evepraisal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Appraisal {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("totals")
    @Expose
    private Totals totals;

    public String getId() {
        return id;
    }

    public Totals getTotals() {
        return totals;
    }

}
