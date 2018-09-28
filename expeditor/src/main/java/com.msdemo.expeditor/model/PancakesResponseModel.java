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
        "metadata",
        "data"
})

public class PancakesResponseModel {
    @JsonProperty("metadata")
    private Metadata metadata;
    @JsonProperty("data")
    private List<String> data;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("metadata")
    public void setMetadata(Metadata md) {
        this.metadata = md;
    }

    @JsonProperty("data")
    public List<String> getData() {
        return data;
    }

    @JsonProperty("data")
    public void setData(List<String> d) {
        this.data = d;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return String.format("PancakesResponseModel {metadata: {id: %s, role: %s, timestamp: %s} data: [%s]}", this.metadata.getId(), this.metadata.getRole(), this.metadata.getTimestamp(), this.data.toArray().toString());
    }
}
