package com.github.selvasuriyanadar.tcc.batchdialer.model;

import org.hibernate.annotations.GenericGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.*;

@Entity
public class BatchCallDialMaster {

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "VARCHAR(255)")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String batchCallDialId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Status status = Status.PENDING;

    @OneToMany(cascade = CascadeType.ALL)
    @NotNull
    private List<@Valid BatchCallDialEntry> batchCallDialEntries;

    public String getBatchCallDialId() {
        return batchCallDialId;
    }

    public void setBatchCallDialId(String batchCallDialId) {
        this.batchCallDialId = batchCallDialId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<BatchCallDialEntry> getBatchCallDialEntries() {
        return batchCallDialEntries;
    }

    public void setBatchCallDialEntries(List<BatchCallDialEntry> batchCallDialEntries) {
        this.batchCallDialEntries = batchCallDialEntries;
    }

}
