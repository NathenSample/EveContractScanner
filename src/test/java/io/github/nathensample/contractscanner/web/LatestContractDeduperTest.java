package io.github.nathensample.contractscanner.web;

import io.github.nathensample.contractscanner.controllers.LatestContractDeduper;
import io.github.nathensample.contractscanner.web.worker.RegionContractApiWorker;
import net.troja.eve.esi.ApiException;
import net.troja.eve.esi.model.PublicContractsResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//TODO: Tidy
@RunWith(MockitoJUnitRunner.class)
public class LatestContractDeduperTest {

    @Mock
    private RegionContractApiWorker latestContractRequester;

    @InjectMocks
    private LatestContractDeduper latestContractDeduper;

    @Test
    public void shouldReturnEmptyOnFirstRun() throws ApiException {
        int regionId = 1;
        List<PublicContractsResponse> mockReturn = new ArrayList<>();
        PublicContractsResponse mockResponseOne = mock(PublicContractsResponse.class);
        when(mockResponseOne.getContractId()).thenReturn(1);
        mockReturn.add(mockResponseOne);

        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        List<PublicContractsResponse> firstCall = latestContractDeduper.getLatestContracts(1);

        assertEquals(1, firstCall.size());


    }

    @Test
    public void shouldReturnNoChangeWhenNoRecordAdded() throws ApiException {
        int regionId = 1;
        List<PublicContractsResponse> mockReturn = new ArrayList<>();
        PublicContractsResponse mockResponseOne = mock(PublicContractsResponse.class);
        when(mockResponseOne.getContractId()).thenReturn(1);
        mockReturn.add(mockResponseOne);

        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        latestContractDeduper.getLatestContracts(1);


        List<PublicContractsResponse> secondCall = latestContractDeduper.getLatestContracts(1);

        assertEquals(0, secondCall.size());
    }

    @Test
    public void shouldReturnChangeWhenRecordAdded() throws ApiException {
        int regionId = 1;
        List<PublicContractsResponse> mockReturn = new ArrayList<>();
        PublicContractsResponse mockResponseOne = mock(PublicContractsResponse.class);
        when(mockResponseOne.getContractId()).thenReturn(1);
        mockReturn.add(mockResponseOne);

        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        //Populate the first run
       latestContractDeduper.getLatestContracts(1);

        PublicContractsResponse mockResponseTwo = mock(PublicContractsResponse.class);
        when(mockResponseTwo.getContractId()).thenReturn(2);

        mockReturn.add(mockResponseTwo);

        List<PublicContractsResponse> thirdCall = latestContractDeduper.getLatestContracts(1);

        assertEquals(1, thirdCall.size());
    }

    @Test
    public void shouldReturnChangeWhenRecordAddedEvenIfPreviousHasVanishedAndThereAreNoNewRecords() throws ApiException {
        int regionId = 1;
        List<PublicContractsResponse> mockReturn = new ArrayList<>();
        PublicContractsResponse mockResponseOne = mock(PublicContractsResponse.class);
        when(mockResponseOne.getContractId()).thenReturn(1);
        mockReturn.add(mockResponseOne);

        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        //Populate the first run
        latestContractDeduper.getLatestContracts(1);

        PublicContractsResponse mockResponseTwo = mock(PublicContractsResponse.class);
        when(mockResponseTwo.getContractId()).thenReturn(2);

        mockReturn = new ArrayList<>();
        mockReturn.add(mockResponseTwo);
        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        List<PublicContractsResponse> thirdCall = latestContractDeduper.getLatestContracts(1);

        assertEquals(1, thirdCall.size());
    }

    @Test
    public void shouldReturnChangeWhenRecordAddedEvenIfPreviousHasVanishedAndThereAreNewRecords() throws ApiException {
        int regionId = 1;
        List<PublicContractsResponse> mockReturn = new ArrayList<>();
        PublicContractsResponse mockResponseOne = mock(PublicContractsResponse.class);
        when(mockResponseOne.getContractId()).thenReturn(1);
        mockReturn.add(mockResponseOne);

        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);

        //Populate the first run
        latestContractDeduper.getLatestContracts(1);

        PublicContractsResponse mockResponseTwo = mock(PublicContractsResponse.class);
        when(mockResponseTwo.getContractId()).thenReturn(2);
        PublicContractsResponse mockResponseThree = mock(PublicContractsResponse.class);
        when(mockResponseThree.getContractId()).thenReturn(3);

        mockReturn = new ArrayList<>();
        mockReturn.add(mockResponseTwo);
        mockReturn.add(mockResponseThree);
        when(latestContractRequester.request(regionId)).thenReturn(mockReturn);


        List<PublicContractsResponse> thirdCall = latestContractDeduper.getLatestContracts(1);

        assertEquals(2, thirdCall.size());
    }
}