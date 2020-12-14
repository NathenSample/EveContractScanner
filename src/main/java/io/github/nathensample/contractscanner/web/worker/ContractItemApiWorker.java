package io.github.nathensample.contractscanner.web.worker;

import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.model.PublicContractsItemsResponse;
import net.troja.eve.esi.model.PublicContractsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContractItemApiWorker extends ApiWorker {
    private static final String TRANQUILITY = "tranquility";
    private static final Logger logger = LoggerFactory.getLogger(ContractItemApiWorker.class);

    public List<PublicContractsItemsResponse> request(Integer contractId) throws ApiException {
        //TODO: More logical handling of this object
        ContractsApi contractsApi = new ContractsApi();

        final List<PublicContractsItemsResponse> result = new ArrayList<>();

        //Make the first request

        ApiResponse<List<PublicContractsItemsResponse>> response = update(
                () -> contractsApi.getContractsPublicItemsContractIdWithHttpInfo(contractId, TRANQUILITY, null, null)
        );

        if (response.getData() != null) {
            result.addAll(response.getData());
        } else {
            logger.error("Contract {} appeared to hgave no items in it?", contractId);
        }

        //Check for pages
        Integer xPages = HeaderUtil.getXPages(response.getHeaders());
        if (xPages == null || xPages < 2) { //Better safe than sorry
            //TODO: add region id lookup
            return result;
        }

        //Iterate the pages
        for (int i = 2; i <= xPages; i++) {
            final int page = i;
            List<PublicContractsItemsResponse> pageResponse = update(
                    () -> contractsApi.getContractsPublicItemsContractId(contractId, TRANQUILITY, null, page)
            );
            result.addAll(pageResponse);
        }
        return result;
    }
}
