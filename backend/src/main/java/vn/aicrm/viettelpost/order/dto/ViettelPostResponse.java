package vn.aicrm.viettelpost.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ViettelPostResponse<T>(
    @JsonProperty("status") 
    Integer status,
    @JsonProperty("error") 
    Boolean error,
    @JsonProperty("message") 
    String message,
    @JsonProperty("data") 
    T data 
) {}