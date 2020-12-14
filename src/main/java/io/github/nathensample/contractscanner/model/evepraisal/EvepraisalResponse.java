
package io.github.nathensample.contractscanner.model.evepraisal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EvepraisalResponse {

    @SerializedName("appraisal")
    @Expose
    private Appraisal appraisal;

    public Appraisal getAppraisal() {
        return appraisal;
    }

    public void setAppraisal(Appraisal appraisal) {
        this.appraisal = appraisal;
    }

}
