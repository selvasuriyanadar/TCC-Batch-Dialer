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
public class BatchCallDialImpl {

    @Autowired
    private BatchCallDialRepository batchCallDialRepository;

    @Autowired
    private BatchCallDialEntryRepository batchCallDialEntryRepository;

    @Autowired
    private BatchCallDialConfig batchCallDialConfig;

    @Autowired
    private BatchCallDialIntg batchCallDialIntg;

    @Autowired
    private BatchCallDialRunImpl batchCallDialRunImpl;

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public BatchCallDialMaster create(BatchCallDialMaster batchCallDialMaster) {
        batchCallDialRepository.save(batchCallDialMaster);

        if (batchCallDialRepository.countByStatus(BatchCallDialMaster.Status.IN_PROGRESS).equals(0L)) {
            run(batchCallDialMaster);
        }
        return batchCallDialMaster;
    }

    @Transactional
    public void run(BatchCallDialMaster batchCallDialMaster) {
        batchCallDialRunImpl.runBatchCallDial(batchCallDialMaster);
        verifyAndUpdateBatchCallDialStatus(batchCallDialMaster);
    }

    @Transactional
    public void completeCall(Long id, BatchCallDialEntry.CallStatus callStatus) {
        if (!batchCallDialRepository.existsEntryByIdAndEntryStatus(id, BatchCallDialEntry.Status.IN_PROGRESS)) {
            throw new IllegalStateException("Batch Call Dial Entry cannot be completed as it is not in progress.");
        }

        BatchCallDialEntry batchCallDialEntry = batchCallDialRepository.fetchEntryById(id).get();
        batchCallDialEntry.setStatus(BatchCallDialEntry.Status.COMPLETED);
        batchCallDialEntry.setCallStatus(callStatus);
        batchCallDialEntryRepository.save(batchCallDialEntry);

        BatchCallDialMaster batchCallDialMaster = batchCallDialRunImpl.callEntryOnCurrentBatchDial(batchCallDialEntry.getAgentFollowmeNumber());
        verifyAndUpdateBatchCallDialStatus(batchCallDialMaster);
    }

    private void verifyAndUpdateBatchCallDialStatus(BatchCallDialMaster batchCallDialMaster) {
        if (batchCallDialRepository.countEntryByBatchCallDialIdAndBatchCallDialEntryStatusIn(batchCallDialMaster.getBatchCallDialId(), Arrays.asList(BatchCallDialEntry.Status.PENDING, BatchCallDialEntry.Status.IN_PROGRESS)).equals(0L)) {
            batchCallDialMaster.setStatus(BatchCallDialMaster.Status.COMPLETED);
            batchCallDialRepository.save(batchCallDialMaster);
        }
        if (batchCallDialRepository.countEntryByBatchCallDialIdAndBatchCallDialEntryStatusIn(batchCallDialMaster.getBatchCallDialId(), Arrays.asList(BatchCallDialEntry.Status.IN_PROGRESS)).equals(0L)
                && !batchCallDialRepository.countEntryByBatchCallDialIdAndBatchCallDialEntryStatusIn(batchCallDialMaster.getBatchCallDialId(), Arrays.asList(BatchCallDialEntry.Status.PENDING)).equals(0L)) {
            batchCallDialMaster.setStatus(BatchCallDialMaster.Status.PENDING);
            batchCallDialRepository.save(batchCallDialMaster);
        }
    }

}
