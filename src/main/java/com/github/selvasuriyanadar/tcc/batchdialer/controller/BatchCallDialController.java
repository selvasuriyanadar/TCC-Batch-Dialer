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
    public ResponseEntity get(
            @PathVariable String batchCallDialId) {
        try {
            Optional<BatchCallDialMaster> data = batchCallDialRepository.findByBatchCallDialId(batchCallDialId);
            if (!data.isPresent()) {
                throw new IllegalStateException("Batch Call Dial not found.");
            }
            return ResponseEntity.ok(data.get());
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping(value = "/create")
    public ResponseEntity create(
            @RequestBody BatchCallDialMaster request) {
        try {
            return ResponseEntity.ok(batchCallDialImpl.create(request));
        }
        catch (IllegalArgumentException | ConstraintViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping(value = "/processTataMissedOrAnsweredClickToCall/{callStatus}")
    public ResponseEntity processTataMissedOrAnsweredOutbound(
            @RequestBody String request,
            @PathVariable BatchCallDialEntry.CallStatus callStatus) {
        try {
            JsonObject requestBody = new JsonParser().parse(request).getAsJsonObject();
            batchCallDialImpl.completeCall(requestBody.getAsJsonPrimitive("custom_identifier").getAsLong(), callStatus);
            return ResponseEntity.ok("success");
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @PostMapping(value = "/run/{batchCallDialId}")
    public ResponseEntity run(
            @PathVariable String batchCallDialId) {
        try {
            if (!batchCallDialRepository.existsByBatchCallDialIdAndStatus(batchCallDialId, BatchCallDialMaster.Status.PENDING)) {
                throw new IllegalStateException("Batch Call Dial not found or it is not a Pending dial.");
            }
            BatchCallDialMaster data = batchCallDialRepository.findByBatchCallDialId(batchCallDialId).get();
            batchCallDialImpl.run(data);
            return ResponseEntity.ok("successfully run.");
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

    @DeleteMapping(value = "/delete/{batchCallDialId}")
    public ResponseEntity delete(
            @PathVariable String batchCallDialId) {
        try {
            if (!batchCallDialRepository.existsByBatchCallDialIdAndStatusNot(batchCallDialId, BatchCallDialMaster.Status.IN_PROGRESS)) {
                throw new IllegalStateException("Batch Call Dial not found or it is In Progress.");
            }
            batchCallDialRepository.deleteById(batchCallDialId);
            return ResponseEntity.ok("Successfully deleted.");
        }
        catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
        catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }

}
