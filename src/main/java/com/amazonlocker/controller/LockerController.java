package com.amazonlocker.controller;

import com.amazonlocker.dto.ClearRequest;
import com.amazonlocker.dto.DepositRequest;
import com.amazonlocker.service.LockerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/locker")
@RequiredArgsConstructor
public class LockerController {

    private final LockerService lockerService;

    @PostMapping("/v1/deposit")
    @ResponseStatus(HttpStatus.CREATED)
    public String createDeposit(@Valid @RequestBody DepositRequest request) {
        return lockerService.depositPackage(request.packageSize());
    }

    @GetMapping("/v1/collect")
    public long getPackage(@RequestParam String pin) {
        return lockerService.collectPackage(pin);
    }

    @PostMapping("/v1/clearCompartment")
    public boolean clearComp(@Valid @RequestBody ClearRequest request) {
        return lockerService.clearCompartment(request.compartmentId());
    }
}
