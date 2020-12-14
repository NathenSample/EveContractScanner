package io.github.nathensample.contractscanner.web.worker;


import io.github.nathensample.contractscanner.service.RequestSchedulingService;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.model.PublicContractsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RegionContractApiWorker extends ApiWorker {

    @Autowired
    private RequestSchedulingService requestSchedulingService;
    private static final String TRANQUILITY = "tranquility";
    private static final Logger logger = LoggerFactory.getLogger(RegionContractApiWorker.class);

    public List<PublicContractsResponse> request(Integer regionId) throws ApiException {
        //TODO: More logical handling of this object
        ContractsApi contractsApi = new ContractsApi();

        final List<PublicContractsResponse> result = new ArrayList<>();

        //Make the first request

        ApiResponse<List<PublicContractsResponse>> response = update(
                () -> contractsApi.getContractsPublicRegionIdWithHttpInfo(regionId, TRANQUILITY, null, null)
        );
        logger.info("Request for region {} expires at {}", regionId, response.getHeaders().get("Expires"));
        requestSchedulingService.setUpdateWhenForRegion(response.getHeaders().get("Expires").get(0), regionId);

        result.addAll(response.getData());

        //Check for pages
        Integer xPages = HeaderUtil.getXPages(response.getHeaders());
        if (xPages == null || xPages < 2) { //Better safe than sorry
            //TODO: add region id lookup
            logger.info("Query for region [{}] got [{}] pages and [{}] records.", regionId, 1, result.size());
            return result;
        }

        //Iterate the pages
        for (int i = 2; i <= xPages; i++) {
            final int page = i;
            List<PublicContractsResponse> pageResponse = update(
                    () -> contractsApi.getContractsPublicRegionId(regionId, TRANQUILITY, null, page)
            );
            result.addAll(pageResponse);
        }
        logger.info("Query for region [{}] got [{}] pages and [{}] records.", regionId, xPages, result.size());
        return result;
    }
}
