package io.github.nathensample.contractscanner.util;


import io.github.nathensample.contractscanner.model.evepraisal.EvepraisalResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

@RunWith(MockitoJUnitRunner.class)
public class EvepraisalUtilTest {

    @InjectMocks
    EvepraisalUtil evepraisalUtil;

    Logger logger = LoggerFactory.getLogger(EvepraisalUtilTest.class);

    @Test
    public void test()
    {
        EvepraisalResponse response = evepraisalUtil.submitEvePraisal("Scimitar x2");
        DecimalFormat df = new DecimalFormat("#,###");
        logger.info("Jita buy: " + df.format(response.getAppraisal().getTotals().getBuy()) + " isk");
        logger.info("Jita buy/m3: " + df.format(response.getAppraisal().getTotals().getBuy() / response.getAppraisal().getTotals().getVolume()) + " isk/m3");
        logger.info("Jita sell: " + df.format(response.getAppraisal().getTotals().getSell()) + " isk");
        logger.info("Jita sell/m3: " + df.format(response.getAppraisal().getTotals().getSell() / response.getAppraisal().getTotals().getVolume()) + " isk/m3");
        logger.info("https://evepraisal.com/a/" + response.getAppraisal().getId());
        System.out.println();
    }

}