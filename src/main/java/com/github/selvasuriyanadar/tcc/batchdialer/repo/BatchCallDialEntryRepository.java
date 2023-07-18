package com.github.selvasuriyanadar.tcc.batchdialer.repo;

import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialMaster;
import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialEntry;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import jakarta.validation.Valid;

import java.util.*;

@Repository
public interface BatchCallDialEntryRepository extends JpaRepository<BatchCallDialEntry, Long> {

}
