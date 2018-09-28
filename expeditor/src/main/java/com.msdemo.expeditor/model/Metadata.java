package com.msdemo.expeditor.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "role",
        "cmd",
        "timestamp",
        "contributors"
})

public class Metadata {
    @JsonProperty("timestamp")
    private int timestamp;
    @JsonProperty("id")
    private String id;
    @JsonProperty("role")
    private String role;
    @JsonProperty("cmd")
    private String cmd;
    @JsonProperty("contributors")
    private List<String> contributors;

    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("timestamp")
    public int getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(int ts) {
        this.timestamp = ts;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String r) {
        this.role = r;
    }

    @JsonProperty("cmd")
    public String getCmd() {
        return cmd;
    }

    @JsonProperty("cmd")
    public void setCmd(String c) {
        this.cmd = c;
    }

    @JsonProperty("contributors")
    public List<String> getContributors() {
        return contributors;
    }

    @JsonProperty("contributors")
    public void setContributors(List<String> c) {
        this.contributors = c;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

