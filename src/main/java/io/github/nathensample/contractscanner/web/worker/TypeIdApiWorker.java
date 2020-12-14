package io.github.nathensample.contractscanner.web.worker;

import io.swagger.annotations.Api;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.ApiResponse;
import net.troja.eve.esi.HeaderUtil;
import net.troja.eve.esi.api.ContractsApi;
import net.troja.eve.esi.api.UniverseApi;
import net.troja.eve.esi.model.PublicContractsItemsResponse;
import net.troja.eve.esi.model.TypeResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TypeIdApiWorker extends ApiWorker {
    private static final String TRANQUILITY = "tranquility";
    private static final String ENUS = "en-us";
    private static final Logger logger = LoggerFactory.getLogger(TypeIdApiWorker.class);

    public TypeResponse request(Integer typeId) throws ApiException {
        //TODO: More logical handling of this object
        UniverseApi universeApi = new UniverseApi();

        final List<PublicContractsItemsResponse> result = new ArrayList<>();

        //Make the first request

        TypeResponse response = update(
                () -> universeApi.getUniverseTypesTypeId(typeId, ENUS, TRANQUILITY, null, null)
        );
        return response;
    }
}
