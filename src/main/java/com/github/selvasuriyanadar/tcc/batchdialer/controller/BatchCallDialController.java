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
        Optional<BatchCallDialMaster> data = batchCallDialRepository.findByBatchCallDialId(batchCallDialId);
        if (!data.isPresent()) {
            throw new IllegalStateException("Batch Call Dial not found.");
        }
        return ResponseEntity.ok(data.get());
    }

    @PostMapping(value = "/create")
    public ResponseEntity create(
            @RequestBody BatchCallDialMaster request) {
        try {
            return ResponseEntity.ok(batchCallDialImpl.create(request));
        }
        catch (IllegalArgumentException ex) {
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

    @DeleteMapping(value = "/delete/{batchCallDialId}")
    public ResponseEntity delete(
            @PathVariable String batchCallDialId) {
        if (!batchCallDialRepository.findByBatchCallDialId(batchCallDialId).isPresent()) {
            throw new IllegalStateException("Batch Call Dial not found.");
        }
        batchCallDialRepository.deleteById(batchCallDialId);
        return ResponseEntity.ok("Successfully deleted.");
    }

}
