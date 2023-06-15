package com.github.selvasuriyanadar.tcc.batchdialer.repo;

import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialMaster;
import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialEntry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;

import java.util.*;

public interface BatchCallDialRepository extends JpaRepository<BatchCallDialMaster, String> {

    public BatchCallDialMaster save(@Valid BatchCallDialMaster batchCallDialMaster);

    public boolean existsByBatchCallDialId(String batchCallDialId);

    public boolean existsByBatchCallDialIdAndStatus(String batchCallDialId, BatchCallDialMaster.Status status);

    public boolean existsByBatchCallDialIdAndStatusNot(String batchCallDialId, BatchCallDialMaster.Status status);

    public Long countByStatus(BatchCallDialMaster.Status status);

    public Optional<BatchCallDialMaster> findByBatchCallDialId(String batchCallDialId);

    public List<BatchCallDialMaster> findByStatus(BatchCallDialMaster.Status status);

    @Query(value = "select case when (count(b) > 0)  then true else false end from BatchCallDialMaster a join a.batchCallDialEntries b where b.id = ?1 and b.status = ?2")
    public boolean existsEntryByIdAndEntryStatus(Long id, BatchCallDialEntry.Status status);

    @Query(value = "select b From BatchCallDialMaster a join a.batchCallDialEntries b where a.batchCallDialId = ?1 and b.status = ?2")
    public List<BatchCallDialEntry> fetchEntryByBatchCallDialIdAndBatchCallDialEntryStatus(String batchCallDialId, BatchCallDialEntry.Status status, Pageable pageable);

    @Query(value = "select count(b) from BatchCallDialMaster a join a.batchCallDialEntries b where a.batchCallDialId = ?1 and b.status in ?2")
    public Long countEntryByBatchCallDialIdAndBatchCallDialEntryStatusIn(String batchCallDialId, List<BatchCallDialEntry.Status> statuses);

    @Query(value = "select b From BatchCallDialMaster a join a.batchCallDialEntries b where b.id = ?1")
    public Optional<BatchCallDialEntry> fetchEntryById(Long id);

}
