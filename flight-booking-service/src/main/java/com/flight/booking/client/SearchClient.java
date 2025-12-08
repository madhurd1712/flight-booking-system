package com.flight.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "search-service", url = "http://localhost:8081/api/flights")
public interface SearchClient {
    @GetMapping("/{id}/availability")
    boolean checkAvailability(@PathVariable("id") String id, @RequestParam("seats") int seats);
}
