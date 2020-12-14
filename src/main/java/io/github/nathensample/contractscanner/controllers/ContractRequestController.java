package io.github.nathensample.contractscanner.controllers;

import com.squareup.okhttp.*;
import io.github.nathensample.contractscanner.model.evepraisal.EvepraisalResponse;
import io.github.nathensample.contractscanner.util.EvepraisalUtil;
import io.github.nathensample.contractscanner.web.worker.ContractItemApiWorker;
import io.github.nathensample.contractscanner.web.worker.TypeIdApiWorker;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.PublicContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;

@Component
public class ContractRequestController {

    // Get the configured regions
    // Ask RegionalController for updates from that region
    // Should ask for the region when the CacheMonitorService notifies it
    // Sends updates to UpdateController
    private static final Logger logger = LoggerFactory.getLogger(ContractRequestController.class);

    @Autowired
    private RegionalController regionalController;
    @Autowired
    ContractItemApiWorker contractItemApiWorker;
    @Autowired
    TypeIdApiWorker typeIdApiWorker;
    @Autowired
    EvepraisalUtil evepraisalUtil;

    public void performScan(int regionId)
    {
        try {
            List<PublicContractsResponse> changeset = regionalController.getContractsForRegion(regionId);

            // Would actually send the changeset to the updateController
            for (PublicContractsResponse curResponse : changeset) {
                try {
                    if (curResponse.getDateExpired().compareTo(OffsetDateTime.now(curResponse.getDateExpired().getOffset())) < 0) {
                        logger.info("Contract {} expired", curResponse.getContractId());
                        continue;
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    List<PublicContractsItemsResponse> items = contractItemApiWorker.request(curResponse.getContractId());
                    for (PublicContractsItemsResponse itRes : items) {
                        stringBuilder.append(typeIdApiWorker.request(itRes.getTypeId()).getName() + " x" + itRes.getQuantity() + "\n");
                    }
                    EvepraisalResponse response = evepraisalUtil.submitEvePraisal(stringBuilder.toString());

                    DecimalFormat df = new DecimalFormat("#,###");

                    if (curResponse.getBuyout() != null) {
                        calculateMargin(curResponse.getBuyout(), response.getAppraisal().getTotals().getBuy(), response, curResponse);
                    } else if (curResponse.getPrice() != null) {
                        calculateMargin(curResponse.getPrice(), response.getAppraisal().getTotals().getBuy(), response, curResponse);
                    }
                } catch (Exception e)
                {
                    logger.info("API error had to skip a contract");
                }
            }
            logger.info("Run finished");

        } catch (Exception e) {
            //Deal with properly
            e.printStackTrace();
        }

    }

    private void calculateMargin(Double buyout, float jitaBuy, EvepraisalResponse response, PublicContractsResponse curResponse)
    {
        BigDecimal buyoutMargin = new BigDecimal(buyout * 1.2);

        BigDecimal profit = new BigDecimal(jitaBuy).subtract(new BigDecimal(buyout));
        if (buyoutMargin.compareTo(new BigDecimal(jitaBuy)) < 0 && response.getAppraisal().getTotals().getBuy() > 5000000)
        {
           // logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            logger.info("https://evepraisal.com/a/" + response.getAppraisal().getId());
            logger.info("<url=contract:{}//{}>An interesting contract</url>", curResponse.getStartLocationId(), curResponse.getContractId());
           // logger.info("THIS CONTRACT IS A 20% MARGIN TO JITA BUY");
            //logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            //
            OkHttpClient okHttpClient = new OkHttpClient();
            String blah = "{\n" +
                    "    \"content\": \"https://evepraisal.com/a/" + response.getAppraisal().getId() + "\\n" +
                    String.format("<url=contract:%d//%d>An interesting contract</url>", curResponse.getStartLocationId(), curResponse.getContractId()) +
                    "\"\n}";
            RequestBody reqbody = RequestBody.create(MediaType.parse("application/json"), new Embed(response.getAppraisal().getId(), curResponse.getStartLocationId(), curResponse.getContractId(), profit, curResponse.getVolume()).toString());
            Request request = new Request.Builder()
                    .url("https://discordapp.com/api/webhooks/715133013138341928/iEQbrXWFRtnwlSxjTkHHOtbBD8_jyDyv2a_DgYoU7fTdhMl4uGv9zWw8u-TtnAdKEi8L")
                    .post(reqbody)
                    .build();
            try {
                Response x = okHttpClient.newCall(request).execute();
                System.out.println();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
        else {
            //logger.info("not a 20% margin");
        }
    }
}
