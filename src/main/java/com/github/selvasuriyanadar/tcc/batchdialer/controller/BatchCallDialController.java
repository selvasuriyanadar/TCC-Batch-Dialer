package com.github.selvasuriyanadar.tcc.batchdialer.controller;

import com.github.selvasuriyanadar.tcc.batchdialer.impl.BatchCallDialImpl;
import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialMaster;
import com.github.selvasuriyanadar.tcc.batchdialer.model.BatchCallDialEntry;
import com.github.selvasuriyanadar.tcc.batchdialer.repo.BatchCallDialRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.validation.ConstraintViolationException;

import java.util.*;

@RestController
@RequestMapping(value = "/batchCallDial")
public class BatchCallDialController {

    @Autowired
    private BatchCallDialImpl batchCallDialImpl;

    @Autowired
    private BatchCallDialRepository batchCallDialRepository;

    @GetMapping(value = "/get/{batchCallDialId}")
    public BatchCallDialMaster get(
            @PathVariable String batchCallDialId) {
        Optional<BatchCallDialMaster> data = batchCallDialRepository.findByBatchCallDialId(batchCallDialId);
        if (!data.isPresent()) {
            throw new IllegalStateException("Batch Call Dial not found.");
        }
        return data.get();
    }

    @PostMapping(value = "/create")
    public BatchCallDialMaster create(
            @RequestBody BatchCallDialMaster request) {
        return batchCallDialImpl.create(request);
    }

    @PostMapping(value = "/processTataMissedOrAnsweredClickToCall/{callStatus}")
    public String processTataMissedOrAnsweredOutbound(
            @RequestBody String request,
            @PathVariable BatchCallDialEntry.CallStatus callStatus) {
        JsonObject requestBody = new JsonParser().parse(request).getAsJsonObject();
        batchCallDialImpl.completeCall(requestBody.getAsJsonPrimitive("custom_identifier").getAsLong(), callStatus);
        return "success";
    }

    @PostMapping(value = "/run/{batchCallDialId}")
    public String run(
            @PathVariable String batchCallDialId) {
        if (!batchCallDialRepository.existsByBatchCallDialIdAndStatus(batchCallDialId, BatchCallDialMaster.Status.PENDING)) {
            throw new IllegalStateException("Batch Call Dial not found or it is not a Pending dial.");
        }
        BatchCallDialMaster data = batchCallDialRepository.findByBatchCallDialId(batchCallDialId).get();
        batchCallDialImpl.run(data);
        return "successfully run.";
    }

    @DeleteMapping(value = "/delete/{batchCallDialId}")
    public String delete(
            @PathVariable String batchCallDialId) {
        if (!batchCallDialRepository.existsByBatchCallDialIdAndStatusNot(batchCallDialId, BatchCallDialMaster.Status.IN_PROGRESS)) {
            throw new IllegalStateException("Batch Call Dial not found or it is In Progress.");
        }
        batchCallDialRepository.deleteById(batchCallDialId);
        return "Successfully deleted.";
    }

}
