package com.refugeeintegration.backend.controller;

import com.refugeeintegration.backend.service.PineconeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pinecone")
@RequiredArgsConstructor
public class PineconeController<SomeDataRequest> {
    private final PineconeService pineconeService;

    @PostMapping("/upsert")
    public ResponseEntity<Void> upsert(@RequestBody SomeDataRequest request) {
        //pineconeService.upsertData(request);
        return ResponseEntity.ok().build();
    }
}