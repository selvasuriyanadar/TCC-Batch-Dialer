package com.github.selvasuriyanadar.tcc.batchdialer.impl;

import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialMaster;
import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialEntry;
import com.github.selvasuriyanadar.tcc.batchdialer.repo.BatchCallDialRepository;
import com.github.selvasuriyanadar.tcc.batchdialer.repo.BatchCallDialEntryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import jakarta.persistence.EntityManager;

import java.util.*;
import java.util.stream.Stream;

@Service
public class BatchCallDialRunImpl {

    @Autowired
    private BatchCallDialRepository batchCallDialRepository;

    @Autowired
    private BatchCallDialEntryRepository batchCallDialEntryRepository;

    @Autowired
    private BatchCallDialConfig batchCallDialConfig;

    @Autowired
    private BatchCallDialIntg batchCallDialIntg;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void runBatchCallDial(BatchCallDialMaster batchCallDialMaster) {
        if (!batchCallDialRepository.countByStatus(BatchCallDialMaster.Status.IN_PROGRESS).equals(0L)) {
            throw new IllegalStateException("Already a Batch Call Dial is in progress.");
        }
        if (!batchCallDialRepository.existsByBatchCallDialIdAndStatus(batchCallDialMaster.getBatchCallDialId(), BatchCallDialMaster.Status.PENDING)) {
            throw new IllegalStateException("Could not find the Pending Batch Call Dial.");
        }

        batchCallDialMaster.setStatus(BatchCallDialMaster.Status.IN_PROGRESS);
        batchCallDialRepository.save(batchCallDialMaster);

        List<String> agentFollowmeNumbers = new ArrayList<>(fetchAgentFollowmeNumbers());
        List<BatchCallDialEntry> batchCallDialEntries = batchCallDialRepository.fetchEntryByBatchCallDialIdAndBatchCallDialEntryStatus(batchCallDialMaster.getBatchCallDialId(), BatchCallDialEntry.Status.PENDING, PageRequest.of(0, agentFollowmeNumbers.size()));
        for (BatchCallDialEntry batchCallDialEntry : batchCallDialEntries) {
            String agentFollowmeNumber = agentFollowmeNumbers.remove(0);
            System.out.println("Calling with agent " + agentFollowmeNumber);
            call(batchCallDialEntry, agentFollowmeNumber);
        }
    }

    @Transactional
    public BatchCallDialMaster callEntryOnCurrentBatchDial(String agentFollowmeNumber) {
        if (batchCallDialRepository.countByStatus(BatchCallDialMaster.Status.IN_PROGRESS).equals(0L)) {
            throw new IllegalStateException("No Batch Call Dial is in progress.");
        }

        BatchCallDialMaster batchCallDialMaster = batchCallDialRepository.findByStatus(BatchCallDialMaster.Status.IN_PROGRESS).get(0);
        List<BatchCallDialEntry> batchCallDialEntries = batchCallDialRepository.fetchEntryByBatchCallDialIdAndBatchCallDialEntryStatus(batchCallDialMaster.getBatchCallDialId(), BatchCallDialEntry.Status.PENDING, PageRequest.of(0, 1));

        if (batchCallDialEntries.size() == 0) {
            return batchCallDialMaster;
        }
        BatchCallDialEntry batchCallDialEntry = batchCallDialEntries.get(0);
        call(batchCallDialEntry, agentFollowmeNumber);
        return batchCallDialMaster;
    }

    private void call(BatchCallDialEntry batchCallDialEntry, String agentFollowmeNumber) {
        if (batchCallDialIntg.callDummy(batchCallDialConfig.fetchAuthorizationBearerToken(), batchCallDialEntry.getId(), agentFollowmeNumber, batchCallDialEntry.getPhone().toString())) {
            batchCallDialEntry.setStatus(BatchCallDialEntry.Status.IN_PROGRESS);
            batchCallDialEntry.setAgentFollowmeNumber(agentFollowmeNumber);
            batchCallDialEntryRepository.save(batchCallDialEntry);
        }
    }

    private List<String> fetchAgentFollowmeNumbers() {
        return batchCallDialIntg.fetchAgentFollowmeNumbers(batchCallDialConfig.fetchAuthorizationBearerToken(), Optional.empty());
    }

}
