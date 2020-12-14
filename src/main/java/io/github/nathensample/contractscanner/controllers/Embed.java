package io.github.nathensample.contractscanner.controllers;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class Embed {

    private String evepraisalId;
    private Long startId;
    private int contractID;
    private BigDecimal estimatedProfit;
    private Double volume;

    public Embed(String evepraisalId, Long startId, int contractID, BigDecimal estimatedProfit, Double volume)
    {
        this.evepraisalId = evepraisalId;
        this.startId = startId;
        this.contractID = contractID;
        this.estimatedProfit = estimatedProfit;
        this.volume = volume;
    }

    @Override
    public String toString()
    {
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        return
        "{\n" +
        "  \"embeds\": [{\n" +
        "    \"title\": \"Ingame contract!\"," +
        "    \"description\": \"https://evepraisal.com/a/" + evepraisalId + "\\n Ingame Contract:\\n " +
        String.format("<url=contract:%d//%d>An interesting contract</url>", startId, contractID) + "\\n Profit: " +
        formatter.format(estimatedProfit)+ "\\nProfit per m3: " +
        formatter.format(estimatedProfit.divide(new BigDecimal(volume))) + "\"" +
        "  }" +
        "]\n" +
        "}"  ;
    }
}
