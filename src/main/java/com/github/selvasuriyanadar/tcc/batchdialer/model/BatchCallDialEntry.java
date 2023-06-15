package com.github.selvasuriyanadar.tcc.batchdialer.model;

import org.hibernate.annotations.GenericGenerator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
public class BatchCallDialEntry {

    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }

    public enum CallStatus {
        MISSED, ANSWERED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Digits(integer = 10, fraction = 0)
    @Positive
    private Long phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Status status = Status.PENDING;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String agentFollowmeNumber;

    @Enumerated(EnumType.STRING)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private CallStatus callStatus;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private ClickToCallResponse clickToCallResponse;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPhone() {
        return this.phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAgentFollowmeNumber() {
        return agentFollowmeNumber;
    }

    public void setAgentFollowmeNumber(String agentFollowmeNumber) {
        this.agentFollowmeNumber = agentFollowmeNumber;
    }

    public CallStatus getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(CallStatus callStatus) {
        this.callStatus = callStatus;
    }

    public ClickToCallResponse getClickToCallResponse() {
        return clickToCallResponse;
    }

    public void setClickToCallResponse(ClickToCallResponse clickToCallResponse) {
        this.clickToCallResponse = clickToCallResponse;
    }

}
