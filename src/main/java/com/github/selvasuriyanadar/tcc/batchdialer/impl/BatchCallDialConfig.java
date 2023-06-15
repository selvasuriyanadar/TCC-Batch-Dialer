package com.github.selvasuriyanadar.tcc.batchdialer.impl;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

@Service
public class BatchCallDialConfig {

    @Value("${com.github.selvasuriyanadar.tcc.batchdialer.tataCloudCallApiAuthorizationToken}")
    private String tataCloudCallApiAuthorizationToken;

    public String fetchAuthorizationBearerToken() {
        return "Bearer " + this.tataCloudCallApiAuthorizationToken;
    }

}
