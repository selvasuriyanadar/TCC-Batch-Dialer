package com.github.selvasuriyanadar.tcc.batchdialer.impl;

import org.springframework.stereotype.Service;
import org.apache.http.client.utils.URIBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

import java.util.*;
import java.util.stream.Stream;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URISyntaxException;
import static java.net.http.HttpResponse.BodyHandlers;
import static java.net.http.HttpRequest.BodyPublishers;

@Service
public class BatchCallDialIntg {

    public boolean callDummy(String authorizationToken, Long batchCallDialEntryId, String agentFollowmeNumber, String destinationNumber) {
        return true;
    }

    public List<String> fetchAgentFollowmeNumbersDummy(String authorizationToken, Optional<String> lastAgentId) {
        return Arrays.asList("+911234567890", "+913457869001", "+912345798890");
    }

    // ---

    public boolean call(String authorizationToken, Long batchCallDialEntryId, String agentFollowmeNumber, String destinationNumber) {
        try {
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("agent_number", agentFollowmeNumber);
            requestBody.addProperty("destination_number", destinationNumber);
            requestBody.addProperty("custom_identifier", batchCallDialEntryId);
            URI uri = new URI("https://api-smartflo.tatateleservices.com/v1/click_to_call");
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", authorizationToken).header("Content-Type", "application/json").POST(BodyPublishers.ofString(new Gson().toJson(requestBody))).build();
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Error status of the http request for click to call tata cloud call : " + response.statusCode());
                System.out.println("Uri : " + uri.toString());
                System.out.println("Response : " + response.body());
                return false;
            }
            return true;
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            return false;
        }
    }

    public List<String> fetchAgentFollowmeNumbers(String authorizationToken, Optional<String> lastAgentId) {
        try {
            URIBuilder uriBuilder = new URIBuilder("https://api-smartflo.tatateleservices.com/v2/agents");
            if (lastAgentId.isPresent()) {
                uriBuilder.setParameter("last_seen_id", lastAgentId.get());
            }
            URI uri = uriBuilder.build();
            HttpRequest request = HttpRequest.newBuilder().uri(uri).header("Authorization", authorizationToken).GET().build();
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                System.out.println("Error status of the http request for fetch agents tata cloud call : " + response.statusCode());
                System.out.println("Uri : " + uri.toString());
                System.out.println("Response : " + response.body());
                throw new IllegalStateException("Could not fetch Agents.");
            }

            JsonObject responseBody = new JsonParser().parse(response.body()).getAsJsonObject();
            List<String> followmeNumbers = new ArrayList<>();
            if (responseBody.has("data")) {
                for (JsonElement agentBody : responseBody.getAsJsonArray("data")) {
                    JsonObject agent = agentBody.getAsJsonObject();
                    String followMeNumber = agent.getAsJsonPrimitive("follow_me_number").getAsString();
                    followmeNumbers.add(followMeNumber);
                    lastAgentId = Optional.of(agent.getAsJsonPrimitive("id").getAsString());
                }
            }
            return Stream.concat(followmeNumbers.stream(), responseBody.has("has_more") && responseBody.getAsJsonPrimitive("has_more").getAsBoolean() ? fetchAgentFollowmeNumbers(authorizationToken, lastAgentId).stream() : Stream.empty()).toList();
        }
        catch (URISyntaxException | IOException | InterruptedException e) {
            throw new IllegalStateException("Could not fetch Agents.");
        }
    }

}
