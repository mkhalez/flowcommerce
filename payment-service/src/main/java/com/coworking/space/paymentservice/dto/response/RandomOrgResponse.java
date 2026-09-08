package com.coworking.space.paymentservice.dto.response;

import java.util.List;

public record RandomOrgResponse(String jsonrpc, Result result, int id) {
    public record Result(RandomData random, int bitsUsed, int bitsLeft,
                         int requestsLeft, int advisoryDelay) {}
    public record RandomData(List<Integer> data, String completionTime) {}
}
