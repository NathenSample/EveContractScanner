
package io.github.nathensample.contractscanner.model.evepraisal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Totals {

    @SerializedName("buy")
    @Expose
    private float buy;
    @SerializedName("sell")
    @Expose
    private float sell;
    @SerializedName("volume")
    @Expose
    private float volume;

    public float getBuy() {
        return buy;
    }

    public void setBuy(float buy) {
        this.buy = buy;
    }

    public float getSell() {
        return sell;
    }

    public void setSell(float sell) {
        this.sell = sell;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

}
