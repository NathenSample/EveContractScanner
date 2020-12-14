package io.github.nathensample.contractscanner.controllers;

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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

@Component
public class RegionalController {

    @Autowired LatestContractDeduper latestContractDeduper;


    private static final Logger logger = LoggerFactory.getLogger(RegionalController.class);

    // Uses the LatestContractDeDuper to get latest updates from a given region
    //TODO: proper exception handling
    public List<PublicContractsResponse> getContractsForRegion(int regionId) throws ApiException {
      return latestContractDeduper.getLatestContracts(regionId);
    }


}
