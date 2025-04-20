package com.refugeeintegration.backend.controller;

import com.refugeeintegration.backend.model.UserSelectionRequest;
import com.refugeeintegration.backend.service.UserSelectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserSelectionController {

    private final UserSelectionService userSelectionService;

    @PostMapping("/updateSelection")
    public ResponseEntity<Void> updateSelection(@RequestBody UserSelectionRequest request) {
        userSelectionService.updateUserSelection(request);
        return ResponseEntity.ok().build();
    }
}
