package io.github.nathensample.contractscanner.web.worker;

import net.troja.eve.esi.ApiException;

public class ApiWorker {
    //TODO: Extract to config
    private static final int MAX_RETRIES = 3;

    protected <T> T update(Update<T> update) throws ApiException {
        return update(update, 0);
    }

    private <T> T update(Update<T> update, int retry) throws ApiException {
        try {
            return update.update();
        } catch (ApiException ex) {
            if (retry < MAX_RETRIES) {
                retry++;
                return update(update, retry);
            } else {
                throw ex;
            }
        }
    }

    protected interface Update<T> {
        public T update() throws ApiException;
    }
}
