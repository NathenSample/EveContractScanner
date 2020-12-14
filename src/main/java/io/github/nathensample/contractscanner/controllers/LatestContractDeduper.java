package io.github.nathensample.contractscanner.controllers;

import io.github.nathensample.contractscanner.web.worker.RegionContractApiWorker;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.PublicContractsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class LatestContractDeduper {

    @Autowired
    private RegionContractApiWorker latestContractRequester;

    private static final Logger logger = LoggerFactory.getLogger(LatestContractDeduper.class);
    private Map<Integer, Integer> historicLastContract = new HashMap<>();

    public List<PublicContractsResponse> getLatestContracts(Integer regionId) throws ApiException {
        List<PublicContractsResponse> allContracts = latestContractRequester.request(regionId);

        if (!historicLastContract.containsKey(regionId)){
            logger.info("First run for region [{}] returning all contracts.", regionId);
            updateHistoricLast(regionId, allContracts);
            return allContracts;
        }

        //TODO: Remove
        debugIdChecking(allContracts);

        //TODO: Break into its own method to avoid use of break;
        int i = 0;
        boolean found = false;
        List<PublicContractsResponse> changeset = new ArrayList<>();
        //TODO: redo this entire thing
        for (; i < allContracts.size(); i++)
        {
            if (!found) {
                if (allContracts.get(i).getContractId().equals(historicLastContract.get(regionId))) {
                    //We've found the previous last
                    found = true;
                } else if (allContracts.get(i).getContractId() > historicLastContract.get(regionId))
                {
                    //We've found a contract with a higher value than previous, so set true AND record it as a change
                    found = true;
                    changeset.add(allContracts.get(i));
                }
            } else {
                changeset.add(allContracts.get(i));
            }
        }

        updateHistoricLast(regionId, allContracts);

        logger.info("Query for region [{}] found a changeset of size [{}]", regionId, changeset.size());
        return changeset;
    }

    private void debugIdChecking(List<PublicContractsResponse> allContracts) {
        int id = allContracts.get(0).getContractId();
        for (PublicContractsResponse curResponse : allContracts)
        {
            if (id > curResponse.getContractId())
            {
                logger.info("Contract id [{}] came AFTER [{}] in the array", curResponse.getContractId(), id);
            }
            id = curResponse.getContractId();
        }
    }

    private void updateHistoricLast(Integer regionId, List<PublicContractsResponse> allContracts) {
        historicLastContract.put(regionId, allContracts.get(allContracts.size() - 1).getContractId());
        //TODO: add region lookup
        logger.info("Latest contract ID for region [{}] updated to id [{}] ", regionId, historicLastContract.get(regionId));
    }


}
